using Microsoft.EntityFrameworkCore;
using Serilog;
using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using System;
using Microsoft.Extensions.Configuration;
using PortalCorporativo.DATA.DatabaseContext;
using PortalCorporativo.DATA.Interfaces.PPs;
using PortalCorporativo.DATA.Interfaces.Projetos;
using PortalCorporativo.DATA.Interfaces.SIC;
using PortalCorporativo.DATA.Interfaces;
using PortalCorporativo.DATA.Repositories.PPs;
using PortalCorporativo.DATA.Repositories.Projetos;
using PortalCorporativo.DATA.Repositories.SIC;
using PortalCorporativo.DATA.Repositories;
using Microsoft.Extensions.Hosting;
using PortalCorporativo.BLL.Interfaces;
using PortalCorporativo.BLL.Services;
using PortalCorporativo.DATA.Middlewares;
using Serilog.Sinks.MSSqlServer;
using System.Data;
using Microsoft.Extensions.Logging;
using PortalCorporativo.DATA.Interfaces.ProjetosExternos;
using PortalCorporativo.DATA.Repositories.ProjetosExternos;
using Serilog.Debugging;
using System.Diagnostics;
using PortalCorporativo.DATA.Interfaces.Projetech;
using PortalCorporativo.DATA.Repositories.Projetech;
using PortalCorporativo.DATA.Interfaces.ProjetosEletronicos;
using PortalCorporativo.DATA.Repositories.ProjetosEletronicos;
using PortalCorporativo.DATA.Domain;
using Microsoft.AspNetCore.Authentication.Negotiate;
using PortalCorporativo.BLL.Authorization;
using PortalCorporativo.DATA.Interfaces.TigerTeam;
using PortalCorporativo.DATA.Repositories.TigerTeam;
using Microsoft.AspNetCore.Http;

var builder = WebApplication.CreateBuilder(args);
ConfigurationManager configuration = builder.Configuration;

// Add services to the container.
var connectionString = builder.Configuration.GetConnectionString("PortalCorporativoConnection") ?? throw new InvalidOperationException("Connection string 'PortalCorporativoConnection' not found.");

builder.Services
    .AddDbContext<PortalCorporativoContext>(
    options => options.UseSqlServer(connectionString, 
    b =>
    {
        b.MigrationsAssembly("PortalCorporativo.UI").UseCompatibilityLevel(120);
        b.UseQuerySplittingBehavior(QuerySplittingBehavior.SplitQuery);
    }));

builder.Services
    .AddAuthentication(NegotiateDefaults.AuthenticationScheme)
    .AddNegotiate();

builder.Services.AddAuthorization(options =>
{
    options.FallbackPolicy = options.DefaultPolicy;
});

builder.Services.AddRazorPages()
    .AddRazorRuntimeCompilation();

builder.Services.AddControllersWithViews();

#region Policy/Permissions

builder.Services.AddAuthorizationBuilder()

    .AddPolicy(PolicyNameConstants.TIGER,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [
                    ActiveDirectoryGroups.ADMIN,
                    ActiveDirectoryGroups.TIGERTEAM_ANALISTA,
                    ActiveDirectoryGroups.TIGERTEAM_SUPERVISOR,
                    ActiveDirectoryGroups.TIGERTEAM_GESTOR])))

    .AddPolicy(PolicyNameConstants.TIGERTEAM_ANALISTA,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [
                    ActiveDirectoryGroups.TIGERTEAM_ANALISTA,
                    ActiveDirectoryGroups.TIGERTEAM_SUPERVISOR,
                    ActiveDirectoryGroups.TIGERTEAM_GESTOR,
                    ActiveDirectoryGroups.ADMIN,
                    ActiveDirectoryGroups.PROJETECH])))

    .AddPolicy(PolicyNameConstants.TIGERTEAM_SUPERVISOR,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [
                    ActiveDirectoryGroups.TIGERTEAM_SUPERVISOR,
                    ActiveDirectoryGroups.TIGERTEAM_GESTOR,
                    ActiveDirectoryGroups.ADMIN,
                    ActiveDirectoryGroups.PROJETECH])))

    .AddPolicy(PolicyNameConstants.TIGERTEAM_GESTOR,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [
                    ActiveDirectoryGroups.TIGERTEAM_GESTOR,
                    ActiveDirectoryGroups.ADMIN,
                    ActiveDirectoryGroups.PROJETECH])))

    .AddPolicy(PolicyNameConstants.AREA_PROJETOS,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [
                    ActiveDirectoryGroups.ADMIN,
                    ActiveDirectoryGroups.PPS_PO,
                    ActiveDirectoryGroups.ESTRUTURANTE_PO,
                    ActiveDirectoryGroups.NEGOCIO_PO,
                    ActiveDirectoryGroups.SEG_EMPRESARIAL_PO,
                    ActiveDirectoryGroups.PROJETECH,
                    ActiveDirectoryGroups.TIGERTEAM_ANALISTA,
                    ActiveDirectoryGroups.TIGERTEAM_SUPERVISOR,
                    ActiveDirectoryGroups.TIGERTEAM_GESTOR,
                    ActiveDirectoryGroups.GESTOR_PO])))

    .AddPolicy(PolicyNameConstants.PP,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [
                    ActiveDirectoryGroups.COMMON,
                    ActiveDirectoryGroups.ADMIN,
                    ActiveDirectoryGroups.PPS_PO,
                    ActiveDirectoryGroups.ESTRUTURANTE_PO,
                    ActiveDirectoryGroups.NEGOCIO_PO,
                    ActiveDirectoryGroups.SEG_EMPRESARIAL_PO,
                    ActiveDirectoryGroups.PROJETECH,
                    ActiveDirectoryGroups.TIGERTEAM_ANALISTA,
                    ActiveDirectoryGroups.TIGERTEAM_SUPERVISOR,
                    ActiveDirectoryGroups.TIGERTEAM_GESTOR,
                    ActiveDirectoryGroups.GESTOR_PO])))

    .AddPolicy(PolicyNameConstants.PP_ADMIN,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [
                    ActiveDirectoryGroups.ADMIN, 
                    ActiveDirectoryGroups.PPS_PO, 
                    ActiveDirectoryGroups.GESTOR_PO, 
                    ActiveDirectoryGroups.PROJETECH])))

    .AddPolicy(PolicyNameConstants.ESTRUTURANTE,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [
                    ActiveDirectoryGroups.COMMON,
                    ActiveDirectoryGroups.ADMIN,
                    ActiveDirectoryGroups.PPS_PO,
                    ActiveDirectoryGroups.ESTRUTURANTE_PO,
                    ActiveDirectoryGroups.NEGOCIO_PO,
                    ActiveDirectoryGroups.SEG_EMPRESARIAL_PO,
                    ActiveDirectoryGroups.PROJETECH,
                    ActiveDirectoryGroups.TIGERTEAM_ANALISTA,
                    ActiveDirectoryGroups.TIGERTEAM_SUPERVISOR,
                    ActiveDirectoryGroups.TIGERTEAM_GESTOR,
                    ActiveDirectoryGroups.GESTOR_PO])))

    .AddPolicy(PolicyNameConstants.ESTRUTURANTE_ADMIN,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [
                    ActiveDirectoryGroups.ADMIN, 
                    ActiveDirectoryGroups.ESTRUTURANTE_PO,
                    ActiveDirectoryGroups.GESTOR_PO,
                    ActiveDirectoryGroups.PROJETECH])))

    .AddPolicy(PolicyNameConstants.EXTERNO,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [
                    ActiveDirectoryGroups.COMMON,
                    ActiveDirectoryGroups.ADMIN,
                    ActiveDirectoryGroups.PPS_PO,
                    ActiveDirectoryGroups.ESTRUTURANTE_PO,
                    ActiveDirectoryGroups.NEGOCIO_PO,
                    ActiveDirectoryGroups.SEG_EMPRESARIAL_PO,
                    ActiveDirectoryGroups.TIGERTEAM_ANALISTA,
                    ActiveDirectoryGroups.TIGERTEAM_SUPERVISOR,
                    ActiveDirectoryGroups.TIGERTEAM_GESTOR,
                    ActiveDirectoryGroups.PROJETECH,
                    ActiveDirectoryGroups.GESTOR_PO])))

    .AddPolicy(PolicyNameConstants.EXTERNO_ADMIN,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [
                    ActiveDirectoryGroups.ADMIN,
                    ActiveDirectoryGroups.NEGOCIO_PO,
                    ActiveDirectoryGroups.GESTOR_PO,
                    ActiveDirectoryGroups.PROJETECH])))

    .AddPolicy(PolicyNameConstants.ELETRONICO,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [
                    ActiveDirectoryGroups.COMMON,
                    ActiveDirectoryGroups.ADMIN,
                    ActiveDirectoryGroups.PPS_PO,
                    ActiveDirectoryGroups.ESTRUTURANTE_PO,
                    ActiveDirectoryGroups.NEGOCIO_PO,
                    ActiveDirectoryGroups.SEG_EMPRESARIAL_PO,
                    ActiveDirectoryGroups.PROJETECH,
                    ActiveDirectoryGroups.GESTOR_PO])))

    .AddPolicy(PolicyNameConstants.ELETRONICO_ADMIN,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [
                    ActiveDirectoryGroups.ADMIN,
                    ActiveDirectoryGroups.SEG_EMPRESARIAL_PO,
                    ActiveDirectoryGroups.GESTOR_PO,
                    ActiveDirectoryGroups.PROJETECH])))

    .AddPolicy(PolicyNameConstants.PROJETECH,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [ActiveDirectoryGroups.ADMIN, ActiveDirectoryGroups.PROJETECH])))

    .AddPolicy(PolicyNameConstants.SIC,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [ActiveDirectoryGroups.SIC, ActiveDirectoryGroups.ADMIN, ActiveDirectoryGroups.SIC_APROVADORES, ActiveDirectoryGroups.SIC_PO])))

    .AddPolicy(PolicyNameConstants.SIC_APROVADORES,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [ActiveDirectoryGroups.ADMIN, ActiveDirectoryGroups.SIC_APROVADORES])))

    .AddPolicy(PolicyNameConstants.SIC_ADMIN,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [ActiveDirectoryGroups.ADMIN, ActiveDirectoryGroups.SIC_PO])))

    .AddPolicy(PolicyNameConstants.GESTOR,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [ActiveDirectoryGroups.GESTOR_PO, ActiveDirectoryGroups.TIGERTEAM_GESTOR])))

    .AddPolicy(PolicyNameConstants.GESTOR_E_ADMIN,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [ActiveDirectoryGroups.GESTOR_PO, ActiveDirectoryGroups.TIGERTEAM_GESTOR, ActiveDirectoryGroups.ADMIN])))

    .AddPolicy(PolicyNameConstants.ADMIN,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [ActiveDirectoryGroups.ADMIN])))

    .AddPolicy(PolicyNameConstants.COMMON,
        policy => policy.RequireAssertion(
            context => CheckPolicyAssertionHandler.CheckPolicyAssertion(
                context, [
                    ActiveDirectoryGroups.COMMON,
                    ActiveDirectoryGroups.ADMIN,
                    ActiveDirectoryGroups.PPS_PO,
                    ActiveDirectoryGroups.ESTRUTURANTE_PO,
                    ActiveDirectoryGroups.NEGOCIO_PO,
                    ActiveDirectoryGroups.SEG_EMPRESARIAL_PO,
                    ActiveDirectoryGroups.PROJETECH,
                    ActiveDirectoryGroups.SIC,
                    ActiveDirectoryGroups.SIC_APROVADORES,
                    ActiveDirectoryGroups.SIC_PO,
                    ActiveDirectoryGroups.TIGERTEAM_ANALISTA,
                    ActiveDirectoryGroups.TIGERTEAM_SUPERVISOR,
                    ActiveDirectoryGroups.TIGERTEAM_GESTOR,
                    ActiveDirectoryGroups.GESTOR_PO])));
#endregion

#region Services
// Services
builder.Services.AddScoped<IUserService, UserService>();
builder.Services.AddScoped<IUserRolesService, UserRolesService>();
builder.Services.AddScoped<ICommonService, CommonService>();
builder.Services.AddScoped<IPPsService, PPsService>();
builder.Services.AddScoped<IProjetosService, ProjetosService>();
builder.Services.AddScoped<IProjetosExternosService, ProjetosExternosService>();
builder.Services.AddScoped<IMailService, MailService>();
builder.Services.AddScoped<ISicService, SicService>();
builder.Services.AddScoped<IRollbackService, RollbackService>();
builder.Services.AddScoped<IControleEtapasProjetosService, ControleEtapasProjetosService>();
builder.Services.AddScoped<IArquivosBiWeeklyService, ArquivosBiWeeklyService>();
builder.Services.AddScoped<ICalculadoraProjetoService, CalculadoraProjetoService>();
builder.Services.AddScoped<IObservacaoTarefaService, ObservacaoTarefaService>();
builder.Services.AddScoped<IAcompanhamentoTarefaService, AcompanhamentoTarefaService>();
builder.Services.AddScoped<ITarefaService, TarefaService>();
builder.Services.AddScoped<IArquivoBibliotecaService, ArquivoBibliotecaService>();
builder.Services.AddScoped<IProjetosEletronicosService, ProjetosEletronicosService>();
builder.Services.AddScoped<IVisualizarArquivosService, VisualizarArquivosService>();
builder.Services.AddScoped<ISOService, SOService>();
builder.Services.AddScoped<IAcompanhamentoProjetoService, AcompanhamentoProjetosService>();
builder.Services.AddScoped<ITigerTeamService, TigerTeamService>();
#endregion

#region Repositories
// Repositories
// Common
builder.Services.AddScoped<IUserRepository, UserRepository>();
builder.Services.AddScoped<INotificationRepository, NotificationRepository>();
builder.Services.AddScoped<IRollbackRepository, RollbackRepository>();
builder.Services.AddScoped<IControleEtapasProjetosRepository, ControleEtapasProjetosRepository>();
builder.Services.AddScoped<IArquivosBiWeeklyRepository, ArquivosBiWeeklyRepository>();
builder.Services.AddScoped<ICerimoniaProjetoRepository, CerimoniaProjetoRepository>();
builder.Services.AddScoped<IParticipanteCerimoniaProjetoRepository, ParticipanteCerimoniaProjetoRepository>();
builder.Services.AddScoped<IArquivoBibliotecaRepository, ArquivoBibliotecaRepository>();
builder.Services.AddScoped<ICustoProjetoRepository, CustoProjetoRepository>();

// PPs
builder.Services.AddScoped<IPPsRepository, PPsRepository>();
builder.Services.AddScoped<IAnexoPPRepository, AnexoPPRepository>();
builder.Services.AddScoped<IAcompanhamentoPPRepository, AcompanhamentoPPRepository>();
builder.Services.AddScoped<ITorrePPRepository, TorrePPRepository>();
builder.Services.AddScoped<ISlotPPRepository, SlotPPRepository>();
builder.Services.AddScoped<IStakeholderPPRepository, StakeholderPPRepository>();
builder.Services.AddScoped<IVolumetriaProjetoPPRepository, VolumetriaProjetoPPRepository>();
builder.Services.AddScoped<IRestricoesProjetoPPRepository, RestricoesProjetoPPRepository>();
builder.Services.AddScoped<IRelacoesProjetoPPRepository, RelacoesProjetoPPRepository>();
builder.Services.AddScoped<IEntregasPPRepository, EntregasPPRepository>();

// SIC
builder.Services.AddScoped<ISolicitacoesRepository, SolicitacoesRepository>();

// Projetos Estruturantes
builder.Services.AddScoped<IProjetosRepository, ProjetosRepository>();
builder.Services.AddScoped<IAnexoProjetoRepository, AnexoProjetoRepository>();
builder.Services.AddScoped<IAcompanhamentoProjetoRepository, AcompanhamentoProjetoRepository>();
builder.Services.AddScoped<IStakeholderProjetoRepository, StakeholderProjetoRepository>();
builder.Services.AddScoped<IRestricoesProjetoRepository, RestricoesProjetoRepository>();
builder.Services.AddScoped<IRelacoesProjetoRepository, RelacoesProjetoRepository>();
builder.Services.AddScoped<IEntregasProjetosRepository, EntregasProjetosRepository>();

// Projetos Externos
builder.Services.AddScoped<IProjetosExternosRepository, ProjetosExternosRepository>();
builder.Services.AddScoped<IAnexoProjetoExternoRepository, AnexoProjetoExternoRepository>();
builder.Services.AddScoped<IAcompanhamentoProjetoExternoRepository, AcompanhamentoProjetoExternoRepository>();
builder.Services.AddScoped<IStakeholderProjetoExternoRepository, StakeholderProjetoExternoRepository>();
builder.Services.AddScoped<IEntregasProjetosExternosRepository, EntregasProjetosExternosRepository>();

// Projetos Eletrônicos
builder.Services.AddScoped<IProjetosEletronicosRepository, ProjetosEletronicosRepository>();
builder.Services.AddScoped<IAnexoProjetoEletronicoRepository, AnexoProjetoEletronicoRepository>();
builder.Services.AddScoped<IAcompanhamentoProjetoEletronicoRepository, AcompanhamentoProjetoEletronicoRepository>();
builder.Services.AddScoped<IStakeholderProjetoEletronicoRepository, StakeholderProjetoEletronicoRepository>();
builder.Services.AddScoped<IEntregasProjetosEletronicosRepository, EntregasProjetosEletronicosRepository>();
builder.Services.AddScoped<ICustoProjetoEletronicoRepository, CustoProjetoEletronicoRepository>();

// Projetech
builder.Services.AddScoped<IAcompanhamentoTarefaRepository, AcompanhamentoTarefaRepository>();
builder.Services.AddScoped<IObservacaoTarefaRepository, ObservacaoTarefaRepository>();
builder.Services.AddScoped<ITarefaRepository, TarefaRepository>();

// Tiger Team
builder.Services.AddScoped<IAcompanhamentoTigerTeamRepository, AcompanhamentoTigerTeamRepository>();
builder.Services.AddScoped<IAnexoTigerTeamRepository, AnexoTigerTeamRepository>();
builder.Services.AddScoped<IDemandaTigerTeamRepository, DemandaTigerTeamRepository>();
builder.Services.AddScoped<IRetesteRepository, RetesteRepository>();
builder.Services.AddScoped<ITesteTigerTeamRepository, TesteTigerTeamRepository>();
builder.Services.AddScoped<IAcompanhamentoChipRepository, AcompanhamentoChipRepository>();
builder.Services.AddScoped<IChipRepository, ChipRepository>();
#endregion

//Add support to logging with SERILOG
var logger = new LoggerConfiguration()
.ReadFrom.Configuration(configuration)
.Enrich.FromLogContext()
.WriteTo.MSSqlServer(
    connectionString: configuration.GetConnectionString("PortalCorporativoConnection"),
    sinkOptions: new MSSqlServerSinkOptions
    {
        TableName = "Logs",
        AutoCreateSqlDatabase = false,
        AutoCreateSqlTable = true,
    },
    columnOptions: GetColumnOptions()
).CreateLogger();

builder.Logging.ClearProviders();
builder.Logging.AddSerilog(logger);
builder.Host.UseSerilog(logger);
builder.Services.AddSingleton<Serilog.ILogger>(logger);

SelfLog.Enable(msg => Debug.WriteLine(msg));

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseDeveloperExceptionPage();
}
else
{
    app.UseExceptionHandler("/Home/Error");
    app.UseHsts();
}

//Add support to logging request with SERILOG
app.UseSerilogRequestLogging();

app.UseHttpsRedirection();
app.UseStaticFiles();
app.UseRouting();
app.UseAuthentication();
app.UseAuthorization();

//Middlewares
app.UseDatabaseInformation();
app.UseLogContextMiddleware();
app.UseAccessDeniedMiddleware();

// APIs de Projetos (Minimal API)
var projetosApi = app.MapGroup("/api/projetos")
    .RequireAuthorization(PolicyNameConstants.ESTRUTURANTE);


// Endpoint removido - a funcionalidade foi integrada ao GetStatusList do controller ProjetosController

// Rotas MVC
app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Home}/{action=Index}/{id?}");

app.MapRazorPages();

app.Run();


static ColumnOptions GetColumnOptions()
{
    var columnOptions = new ColumnOptions
    {
        AdditionalColumns =
        [
            new SqlColumn
            {
                DataType   = SqlDbType.VarChar,
                ColumnName = "UserId",
                DataLength = 12,
                AllowNull  = true
            },
            new SqlColumn
            {
                DataType   = SqlDbType.VarChar,
                ColumnName = "ControllerName",
                DataLength = 50,
                AllowNull  = true
            },
            new SqlColumn
            {
                DataType   = SqlDbType.VarChar,
                ColumnName = "Action",
                DataLength = 50,
                AllowNull  = true
            }
        ]
    };

    columnOptions.Store.Remove(StandardColumn.Properties);
    columnOptions.Store.Remove(StandardColumn.MessageTemplate);

    return columnOptions;
}
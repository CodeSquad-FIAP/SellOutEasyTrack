using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using PortalCorporativo.BLL.Interfaces;
using PortalCorporativo.Controllers;
using PortalCorporativo.DATA.Domain;
using PortalCorporativo.DATA.Domain.Projetos;
using PortalCorporativo.DATA.DTOs;
using PortalCorporativo.DATA.Enums;
using PortalCorporativo.DATA.Interfaces;
using PortalCorporativo.DATA.Interfaces.Projetos;
using PortalCorporativo.DATA.ViewModels;
using PortalCorporativo.Enums;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace PortalCorporativo.UI.Controllers
{
    [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE)]
    public class ProjetosController : BaseController
    {
        private readonly ILogger<ProjetosController> _logger;
        private readonly IProjetosRepository _ProjetosRepository;
        private readonly IAcompanhamentoProjetoRepository _acompanhamentoProjetoRepository;
        private readonly IAnexoProjetoRepository _anexoProjetoRepository;
        private readonly INotificationRepository _notificationRepository;
        private readonly IStakeholderProjetoRepository _stakeholderProjetoRepository;
        private readonly IRestricoesProjetoRepository _restricoesProjetoProjetoRepository;
        private readonly IRelacoesProjetoRepository _relacoesProjetoProjetoRepository;
        private readonly IEntregasProjetosRepository _entregasProjetosRepository;
        private readonly IUserRepository _userRepository;
        private readonly IProjetosService _ProjetosService;
        private readonly IRollbackService _rollbackService;
        private readonly IControleEtapasProjetosService _controleEtapasProjetosService;
        private readonly IMailService _mailService;
        private readonly IArquivosBiWeeklyService _arquivosBiWeeklyService;
        private readonly ICalculadoraProjetoService _calculadoraProjetoService;
        private readonly IAuthorizationService _authorizationService;
        private readonly IVisualizarArquivosService _visualizarArquivosService;
        private readonly ICommonService _commonService;
        private readonly IUserService _userService;
        private readonly ICustoProjetoRepository _custoProjetoRepository;

        public ProjetosController(
            ILogger<ProjetosController> logger,
            IProjetosRepository ProjetosRepository,
            IAcompanhamentoProjetoRepository acompanhamentoProjetoRepository,
            IAnexoProjetoRepository anexoProjetoRepository,
            IUserRepository userRepository,
            INotificationRepository notificationRepository,
            IStakeholderProjetoRepository stakeholderProjetoRepository,
            IRestricoesProjetoRepository restricoesProjetoProjetoRepository,
            IRelacoesProjetoRepository relacoesProjetoProjetoRepository,
            IEntregasProjetosRepository entregasProjetosRepository,
            IProjetosService ProjetosService,
            IMailService mailService,
            IRollbackService rollbackService,
            IControleEtapasProjetosService controleEtapasProjetosService,
            IArquivosBiWeeklyService arquivosBiWeeklyService,
            ICalculadoraProjetoService calculadoraProjetoService,
            IAuthorizationService authorizationService,
            IVisualizarArquivosService visualizarArquivosService,
            ICommonService commonService,
            IUserService userService,
            ICustoProjetoRepository custoProjetoRepository)
        {
            _logger = logger;
            _ProjetosRepository = ProjetosRepository;
            _acompanhamentoProjetoRepository = acompanhamentoProjetoRepository;
            _anexoProjetoRepository = anexoProjetoRepository;
            _userRepository = userRepository;
            _notificationRepository = notificationRepository;
            _stakeholderProjetoRepository = stakeholderProjetoRepository;
            _restricoesProjetoProjetoRepository = restricoesProjetoProjetoRepository;
            _relacoesProjetoProjetoRepository = relacoesProjetoProjetoRepository;
            _ProjetosService = ProjetosService;
            _mailService = mailService;
            _entregasProjetosRepository = entregasProjetosRepository;
            _rollbackService = rollbackService;
            _controleEtapasProjetosService = controleEtapasProjetosService;
            _arquivosBiWeeklyService = arquivosBiWeeklyService;
            _calculadoraProjetoService = calculadoraProjetoService;
            _authorizationService = authorizationService;
            _visualizarArquivosService = visualizarArquivosService;
            _commonService = commonService;
            _userService = userService;
            _custoProjetoRepository = custoProjetoRepository;
        }

        private async Task<bool> CheckIfHasAdminPermissions()
        {
            var isAdminEvaluationResult = await _authorizationService.AuthorizeAsync(User, PolicyNameConstants.ESTRUTURANTE_ADMIN);
            return isAdminEvaluationResult.Succeeded;
        }

        public ActionResult Dashboard()
        {
            return View();
        }

        public IActionResult ProjectCreated(int Projetoid)
        {
            ViewBag.ProjetoId = Projetoid;
            return View();
        }

        public IActionResult Index()
        {
            _logger.LogWarning($"{HttpContext.Request.Method}/ ESTRUTURANTE - INDEX");
            return View();
        }

        [HttpPost]
        public async Task<IActionResult> GetProjectsJson([FromBody] ProjetosIndexViewModel indexViewModel)
        {
            bool isAdmin = await CheckIfHasAdminPermissions();

            var projetos = await _ProjetosRepository.GetProjetos(
                    userId: GetCurrentUserId(),
                    isAdmin: isAdmin,
                    onlyMyProjects: indexViewModel.OnlyMyProjects,
                    IncludeBacklog: indexViewModel.IncludeBackLog,
                    IncludeCompleted: indexViewModel.IncludeCompleted,
                    IncludeCanceleds: indexViewModel.IncludeCanceled,
                    IncludeScratches: indexViewModel.IncludeScratches,
                    includeOngoing: indexViewModel.IncludeOngoing
                );

            var statusToIgnore = new string[]
            {
                "Rascunho",
                "Concluído",
                "Pós-Produção",
                "Backlog",
                "Cancelado",
                "Transferido PPs",
                "Stand By"
            };

            var mappedProjects = projetos.Select(x => new
            {
                x.Id,
                ValueStream = x.ValueStream?.Nome ?? "Sem Value Stream",
                Data = x.DataCadastro.ToString("dd/MM/yyyy"),
                Titulo = x.NomeProjeto,
                Status = x.EtapaProjeto.Nome,
                AberturaPSS = x.DataAberturaPSS.HasValue ? x.DataAberturaPSS.Value.ToString("dd/MM/yyyy") : "Sem Abertura PSS",
                EncerramentoPSS = x.DataEncerramentoPSS.HasValue ? x.DataEncerramentoPSS.Value.ToString("dd/MM/yyyy") : "Sem Encerramento PSS",
                PO = x.AnalistaResponsavel?.NomeUsuario ?? "Sem Analista Atribuído",
                IsInRollback = (x.IsInRollback || x.EntregasProjetos.Any(x => x.IsInRollback)),
                Outdated = !statusToIgnore.Contains(x.EtapaProjeto.Nome) && (!(x.DataAtualizacao.HasValue) || (x.DataAtualizacao.HasValue && x.DataAtualizacao < DateTime.Now.AddDays(-5))),
                EntregasAndamento = x.EntregasProjetos.Where(x => x.DataConclusao.HasValue == false).Count(),
                EntregasConcluidas = x.EntregasProjetos.Where(x => x.DataConclusao.HasValue).Count()
            });

            return Json(mappedProjects);
        }

        // GET: ProjetosController
        public async Task<IActionResult> IndexOld()
        {
            var isAdmin = await CheckIfHasAdminPermissions();
            var projetos = await _ProjetosRepository.GetProjetos(GetCurrentUserId(), isAdmin);

            _logger.LogWarning($"{HttpContext.Request.Method}/ - ESTRUTURANTE");

            return View(new ProjetosIndexViewModel
            {
                IncludeBackLog = false,
                IncludeCompleted = false,
                IncludeScratches = false,
                IncludeCanceled = false,
                IncludeOngoing = true,
                Projetos = projetos
            });
        }

        // GET: ProjetosController
        [HttpPost]
        public async Task<IActionResult> IndexOld(ProjetosIndexViewModel indexViewModel)
        {
            var isAdmin = await CheckIfHasAdminPermissions();

            var projetos = await _ProjetosRepository.GetProjetos(
                    userId: GetCurrentUserId(),
                    isAdmin: isAdmin,
                    onlyMyProjects: indexViewModel.OnlyMyProjects,
                    IncludeBacklog: indexViewModel.IncludeBackLog,
                    IncludeCompleted: indexViewModel.IncludeCompleted,
                    IncludeScratches: indexViewModel.IncludeScratches,
                    IncludeCanceleds: indexViewModel.IncludeCanceled,
                    includeOngoing: true
                );

            indexViewModel.Projetos = projetos;

            return View(indexViewModel);
        }

        // GET: ProjetosController/Create
        public ActionResult Create()
        {
            _ProjetosService.ConfigViewBag(ViewBag);
            ViewBag.USERAD = GetActiveDirectoryUserInformation();
            _logger.LogWarning($"{HttpContext.Request.Method}/ ESTRUTURANTE - CREATE");
            return View();
        }

        // POST: ProjetosController/Create
        [HttpPost]
        [ValidateAntiForgeryToken]
        public IActionResult Create(Projeto Projeto)
        {
            try
            {
                if (ModelState.IsValid)
                {
                    Projeto.UsuarioCadastroId = GetCurrentUserId();
                    var etapaInicialId = _ProjetosService.GerarIdDaEtapaInicialPreCadastro();
                    Projeto.EtapaProjetoId = etapaInicialId;
                    Projeto.PreencheuSegundaEtapa = false;

                    _ProjetosRepository.Add(Projeto);
                    _ProjetosRepository.SaveChanges();

                    Notify($"Primeira etapa do cadastro concluída. ID:{Projeto.Id}",
                        title: "Sucesso!", notificationType: UINotificationType.success);

                    _logger.LogWarning($"{HttpContext.Request.Method}/ - primeira etapa do cadastro concluída no projeto {Projeto.Id}.");

                    return RedirectToAction("CreateSegundaEtapa", new { idProjeto = Projeto.Id });
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado quando tentava criar um novo projeto.");

                Notify($"Algo deu errado!",
                        title: "Atenção!", notificationType: UINotificationType.warning);
            }

            _ProjetosService.ConfigViewBag(ViewBag);
            ViewBag.USERAD = GetActiveDirectoryUserInformation();

            return View(Projeto);
        }

        public async Task<IActionResult> CreateSegundaEtapa(int idProjeto)
        {
            bool isAdmin = await CheckIfHasAdminPermissions();
            var Projeto = await _ProjetosRepository.GetProjetoById(GetCurrentUserId(), isAdmin, idProjeto);
            if (Projeto == null) return RedirectToAction("Index");
            return View(Projeto);
        }

        [HttpPost, ActionName("CreateSegundaEtapa")]
        public ActionResult CreateSegundaEtapaPost(int Projetoid, string necessarioParecerJurídicoRegulatorio, IFormFile postedFile)
        {
            try
            {
                var Projeto = _ProjetosRepository.Get(Projetoid);

                if (!string.IsNullOrEmpty(necessarioParecerJurídicoRegulatorio) && postedFile == null)
                {
                    Notify("É obrigatório anexar o parecer!");
                    return RedirectToAction(nameof(CreateSegundaEtapa), new { idProjeto = Projetoid });
                }

                if (postedFile != null)
                {
                    var tipoAnexoId = _ProjetosService.PegaIdTipoAnexo("Parecer Jurídico/Regulatório");
                    var anexoProjeto = _ProjetosService.PrepararAnexoParaSalvar(tipoAnexoId, postedFile, GetCurrentUserId(), Projetoid);
                    anexoProjeto.DescricaoAnexo = "Documento Parecer Jurídico/Regulatório";
                    _anexoProjetoRepository.Add(anexoProjeto);
                    _anexoProjetoRepository.SaveChanges();
                    var usuario = _userRepository.GetByRe(GetCurrentUserId());

                    AdicionarAcompanhamentoProjeto(
                        "Inclusão de Anexo", 
                        $"{usuario.NomeUsuario} adicionou um novo anexo: {anexoProjeto.NomeAnexo}", 
                        Projetoid, 
                        TipoAcompanhamento.Automatic,
                        SubTipoAcompanhamento.File_Added);
                }

                Projeto.PreencheuSegundaEtapa = true;
                Projeto.DataAtualizacao = DateTime.Now;

                var etapaInicialId = _ProjetosService.GerarIdDaEtapaInicialPosCadastro();
                Projeto.EtapaProjetoId = etapaInicialId;

                _ProjetosRepository.Update(Projeto);
                _ProjetosRepository.SaveChanges();

                var controleEtapa = new ControleEtapasProjetos()
                {
                    EtapaId = etapaInicialId,
                    Fim = null,
                    TipoProjeto = TipoProjeto.Estruturante,
                    ProjetoId = Projeto.Id
                };
                _controleEtapasProjetosService.AddStep(controleEtapa);                

                Notify(title: "Ok!",
                    message: "Projeto cadastrado com sucesso", notificationType: UINotificationType.success);

                _logger.LogWarning($"{HttpContext.Request.Method}/ - cadastrou projeto estruturante {Projeto.Id}");

                var emailModel = _ProjetosService.MontarEmailCriacaoProjeto(Projeto, Request.Host);
                _mailService.SendEmail(emailModel);

                var globalNotification = new Notification()
                {
                    Title = "Novo Projeto Cadastrado",
                    DataTermino = DateTime.Now.AddDays(2),
                    Description = "Um novo projeto foi cadastrado no sistema!",
                    RedirectLink = $"https://{Request.Host}/Projetos/Edit/{Projeto.Id}",
                    Type = NotificationType.global,
                    GroupName = "estruturantes-admin"
                };
                _notificationRepository.Add(globalNotification);
                _notificationRepository.SaveChanges();

                return RedirectToAction("ProjectCreated", new { Projetoid });
            }
            catch (Exception ex)
            {

                _logger.LogError(ex, "Erro enquanto finalizava segunda etapa preenchimento Projeto");
                Notify("Algo deu errado ao finalizar a etapa");
                return RedirectToAction(nameof(CreateSegundaEtapa), new { idProjeto = Projetoid });
            }
        }


        // GET: ProjetosController/Edit/5
        public async Task<IActionResult> Edit(int? id)
        {
            var isAdmin = await CheckIfHasAdminPermissions();

            if (id == null)
            {
                return NotFound();
            }

            var Projeto = await _ProjetosRepository.GetProjetoById(GetCurrentUserId(), isAdmin, id.Value);

            if (Projeto == null)
                return NotFound();

            if (!Projeto.PreencheuSegundaEtapa)
                return RedirectToAction("CreateSegundaEtapa", new { idProjeto = Projeto.Id });

            if (!isAdmin)
            {
                // Deixa apenas a última SPTI anexada
                var anexosAtualizados = _ProjetosService.ConfigurarAnexosParaSolicitante(Projeto.AnexoProjetos);
                Projeto.AnexoProjetos = anexosAtualizados;
            }

            var rollbacks = await _rollbackService.GetRollbacksByProject(Projeto.Id, TipoProjeto.Estruturante);
            var arquivos = _arquivosBiWeeklyService.GetAll(Projeto.Id, TipoProjeto.Estruturante);
            var cerimonias = _calculadoraProjetoService.GetAll(Projeto.Id, TipoProjeto.Estruturante);
            var custos = await _custoProjetoRepository.GetCostsByProjectId(Projeto.Id, TipoProjeto.Estruturante);

            _ProjetosService.ConfigViewBag(ViewBag);

            ViewBag.ProjetoId = Projeto.Id;
            ViewBag.Rollbacks = rollbacks;
            ViewBag.ArquivosBiWeekly = arquivos;
            ViewBag.Cerimonias = cerimonias;
            ViewBag.Custos = custos;
            ViewBag.USERAD = GetActiveDirectoryUserInformation();

            _logger.LogWarning($"{HttpContext.Request.Method}/ ESTRUTURANTE - EDIT");

            return View(Projeto);
        }

        // POST: ProjetosController/Edit/5
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Edit(int id, Projeto Projeto)
        {
            if (id != Projeto.Id)
            {
                return NotFound();
            }

            if (ModelState.IsValid)
            {
                try
                {
                    var validation = _ProjetosService.ValidarProjeto(Projeto);

                    if (!string.IsNullOrEmpty(validation))
                    {
                        Notify(validation, title: "Atenção!", notificationType: UINotificationType.warning);
                        _ProjetosService.ConfigViewBag(ViewBag);
                        return RedirectToAction(nameof(Edit), id);
                    }

                    Projeto.DataAtualizacao = DateTime.Now;
                    _ProjetosRepository.Update(Projeto);

                    var user = GetActiveDirectoryUserInformation();

                    var mudouEtapa = _commonService.CheckIfFieldHasBeenModified("Projeto", "EtapaProjetoId");

                    if (!string.IsNullOrEmpty(Projeto.AnalistaResponsavelId))
                    {
                        var mudouPO = _commonService.CheckIfFieldHasBeenModified("Projeto", "AnalistaResponsavelId");
                        if (mudouPO)
                        {
                            var po = _userRepository.GetByRe(Projeto.AnalistaResponsavelId);

                            AdicionarAcompanhamentoProjeto(
                                "Mudança de PO",
                                $"{user.Username} mudou PO do projeto para '{po.NomeUsuario}'",
                                Projeto.Id,
                                TipoAcompanhamento.Automatic,
                                SubTipoAcompanhamento.PO_Changed);

                            _logger.LogWarning($"{HttpContext.Request.Method}/ - mudou PO do projeto para '{po.NomeUsuario}'.");
                        }
                    }

                    _ProjetosRepository.SaveChanges();

                    if (mudouEtapa)
                    {
                        var lastStates = new string[]
                        {
                            "Concluído",
                            "Transferido para Estruturantes",
                            "Cancelado",
                            "Transferido para PPs"
                        };
                        
                        var nomeEtapa = await _ProjetosService.PegarNomeEtapaProjeto(Projeto.EtapaProjetoId);

                        if (Projeto.EtapaProjetoId == await _ProjetosService.GetEtapaConcluido())
                            Projeto.DataEncerramentoPSS = DateTime.Now;

                        AdicionarAcompanhamentoProjeto(
                            "Mudança de Etapa", 
                            $"{user.Username} mudou status do projeto para '{nomeEtapa}'", 
                            Projeto.Id, 
                            TipoAcompanhamento.Automatic,
                            SubTipoAcompanhamento.Status_Changed);

                        _logger.LogWarning($"{HttpContext.Request.Method}/ - mudou status do projeto para '{nomeEtapa}'.");

                        var isLastState = lastStates.Contains(nomeEtapa);
                        DateTime? endDate = isLastState ? DateTime.Now : null;

                        // Finaliza Etapa Anterior e Adiciona nova etapa
                        var finishedLastStep = _controleEtapasProjetosService.FinishLastStep(Projeto.Id, TipoProjeto.Estruturante);
                        if (finishedLastStep)
                        {
                            var controleEtapa = new ControleEtapasProjetos()
                            {
                                EtapaId = Projeto.EtapaProjetoId,
                                Fim = endDate,
                                TipoProjeto = TipoProjeto.Estruturante,
                                ProjetoId = Projeto.Id
                            };
                            _controleEtapasProjetosService.AddStep(controleEtapa);
                        }
                    }

                    Notify("Projeto atualizado!", title: "Sucesso!", notificationType: UINotificationType.success);
                    _logger.LogWarning($"{HttpContext.Request.Method}/ - editou o estruturante {Projeto.Id}.");
                    return RedirectToAction(nameof(Edit), id);
                }
                catch (Exception ex)
                {
                    _logger.LogError(ex, "Algo deu errado quando o {user} tentava editar o projeto {ProjetoId}.", GetCurrentUserId(), Projeto.Id);
                    Notify($"Algo deu errado!",
                            title: "Atenção!", notificationType: UINotificationType.warning);
                    return RedirectToAction(nameof(Edit), id);
                }
            }

            Notify($"Algo deu errado para atualizar o projeto!",
                    title: "Atenção!", notificationType: UINotificationType.warning);
            return RedirectToAction(nameof(Edit), id);
        }

        // GET: ProjetosController/Delete/5
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public ActionResult Delete(int? id)
        {
            if (id == null) return NotFound();

            var Projeto = _ProjetosRepository.Get(id.Value);

            if (Projeto == null) return NotFound();

            return View(Projeto);
        }

        // POST: ProjetosController/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public IActionResult DeleteConfirmed(int id)
        {
            try
            {
                var Projeto = _ProjetosRepository.Get(id);
                Projeto.Deleted = true;

                _ProjetosRepository.Update(Projeto);
                _ProjetosRepository.SaveChanges();

                Notify(title: "Ok!", message: "Projeto excluído.", notificationType: UINotificationType.success);
                _logger.LogWarning($"{HttpContext.Request.Method}/ - Estruturante excluído com sucesso. Id: {id}");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado quando tentava apagar o projeto {ProjetoId}.", id);

                Notify($"Algo deu errado!",
                        title: "Atenção!", notificationType: UINotificationType.warning);
            }

            return RedirectToAction(nameof(Index));
        }

        [Authorize(Policy = PolicyNameConstants.GESTOR)]
        public async Task<IActionResult> Relatorio()
        {
            try
            {
                var stream = await _ProjetosService.GerarRelatorioProjetosAsync();
                _logger.LogWarning($"{HttpContext.Request.Method}/ - Extraiu relatório estruturantes.");
                return File(stream, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", $"Relatorio-Estruturantes-{DateTime.Now:dd-MM-yyyy}.xlsx");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro gerado equanto o sistema gerava um relatório");
                Notify("Algo deu errado gerando o relatório de projetos estruturantes");
            }
            return RedirectToAction(nameof(Index));
        }

        public IActionResult AdicionarAnexo(int tipoAnexoId, IFormFile postedFile, int ProjetoId, string descAnexo)
        {
            try
            {
                if (postedFile == null)
                {
                    Notify("Um arquivo deve ser anexado!");
                    return RedirectToAction("Edit", "Projetos", new { id = ProjetoId });
                }

                var anexoProjeto = _ProjetosService.PrepararAnexoParaSalvar(tipoAnexoId, postedFile, GetCurrentUserId(), ProjetoId);
                anexoProjeto.DescricaoAnexo = descAnexo;

                _anexoProjetoRepository.Add(anexoProjeto);
                _anexoProjetoRepository.SaveChanges();

                var usuario = _userRepository.GetByRe(GetCurrentUserId());

                AdicionarAcompanhamentoProjeto(
                    "Inclusão de Anexo", 
                    $"{usuario.NomeUsuario} adicionou um novo anexo: {anexoProjeto.NomeAnexo}", 
                    ProjetoId, 
                    TipoAcompanhamento.Automatic,
                    SubTipoAcompanhamento.File_Added);

                _logger.LogWarning($"{HttpContext.Request.Method}/ adicionou um novo anexo no estruturante {ProjetoId}.");

                Notify("Anexo adicionado", "Sucesso!", UINotificationType.success);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado quando adicionava um novo anexo no projeto {ProjetoId}", ProjetoId);
                Notify("Atenção!", "Algo deu errado ao anexar o arquivo!", UINotificationType.warning);
            }

            return RedirectToAction("Edit", "Projetos", new { id = ProjetoId });
        }

        public IActionResult BaixarAnexo(int fileId)
        {
            AnexoProjeto anexo = _anexoProjetoRepository.Get(fileId);
            _logger.LogWarning($"{HttpContext.Request.Method}/ baixou o anexo {anexo.NomeAnexo}.");
            return File(anexo.Bytes, anexo.ContentType, anexo.NomeAnexo);
        }

        private void AdicionarAcompanhamentoProjeto(
            string titulo, 
            string mensagemAcompanhamento, 
            int ProjetoId, 
            TipoAcompanhamento tipoAcompanhamento,
            SubTipoAcompanhamento subTipoAcompanhamento)
        {
            try
            {
                var acompanhamento = new AcompanhamentoProjeto
                {
                    Data = DateTime.Now,
                    AutorId = GetCurrentUserId(),
                    ProjetoId = ProjetoId,
                    Titulo = titulo,
                    DescricaoAcompanhamento = mensagemAcompanhamento,
                    TipoAcompanhamento = tipoAcompanhamento,
                    SubTipoAcompanhamento = subTipoAcompanhamento
                };

                _acompanhamentoProjetoRepository.Add(acompanhamento);
                _acompanhamentoProjetoRepository.SaveChanges();

                var projeto = _ProjetosRepository.Get(ProjetoId);
                projeto.DataAtualizacao = DateTime.Now;
                _ProjetosRepository.Update(projeto);
                _ProjetosRepository.SaveChanges();
                _logger.LogWarning($"{HttpContext.Request.Method}/ - adicionou acompanhamento {acompanhamento.Id} no projeto {ProjetoId}");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado ao adicionar um acompanhamento para o projeto {ProjetoId}", ProjetoId);
            }
        }

        private void EditarAcompanhamentoProjeto(AcompanhamentoProjeto acompanhamento)
        {
            try
            {
                _acompanhamentoProjetoRepository.Update(acompanhamento);
                _acompanhamentoProjetoRepository.SaveChanges();
                _logger.LogWarning($"{HttpContext.Request.Method}/ - editou acompanhamento {acompanhamento.Titulo} no estruturante {acompanhamento.ProjetoId}.");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado ao editar o acompanhamento {acompanhamentoId} para o estruturante {ProjetoId}",
                    acompanhamento.Id, acompanhamento.ProjetoId);
            }
        }

        private void ApagarAcompanhamentoProjeto(AcompanhamentoProjeto acompanhamento)
        {
            try
            {
                _acompanhamentoProjetoRepository.Remove(acompanhamento);
                _acompanhamentoProjetoRepository.SaveChanges();
                _logger.LogWarning($"{HttpContext.Request.Method}/ - apagou acompanhamento {acompanhamento.Titulo} no estruturante {acompanhamento.ProjetoId}.");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado ao apagar o acompanhamento {acompanhamentoId} para o estruturante {ProjetoId}",
                    acompanhamento.Id, acompanhamento.ProjetoId);
            }
        }

        private void ApagarAnexoProjeto(AnexoProjeto anexo)
        {
            try
            {
                _anexoProjetoRepository.Remove(anexo);
                _anexoProjetoRepository.SaveChanges();
                _logger.LogWarning($"{HttpContext.Request.Method}/ - apagou anexo {anexo.NomeAnexo} no estruturante {anexo.ProjetoId}.");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado ao apagar o acompanhamento {acompanhamentoId} para o estruturante {ProjetoId}",
                    anexo.Id, anexo.ProjetoId);
            }
        }



        [HttpGet]
        public async Task<IActionResult> GetStatusList(TipoProjeto tipoProjeto, int projetoId)
        {
            var statusList = await _ProjetosRepository.GetStatusesForTimeline();

            var tasks = statusList.Select(async status =>
            {
                var dias = await _controleEtapasProjetosService.GetDiasNaEtapa(tipoProjeto, projetoId, status.Id);
                return new { Id = status.Id, Nome = status.Nome, DiasNaEtapa = dias };
            });

            var result = await Task.WhenAll(tasks);
            return Json(result);
        }


        [HttpGet]
        public string GetProjetosByStatus()
        {
            var ProjetosByStatus = _ProjetosService.PegarQuantidadeDeProjetosPorEtapa();
            var ProjetosByStatusJson = JsonConvert.SerializeObject(ProjetosByStatus);
            return ProjetosByStatusJson;
        }

        [HttpGet]
        public string GetProjetosTimeline()
        {
            var ProjetosParaTimeline = _ProjetosService.PegarProjetosParaTimeline();
            var ProjetosParaTimelineJson = JsonConvert.SerializeObject(ProjetosParaTimeline);
            return ProjetosParaTimelineJson;
        }

        [HttpGet]
        public IActionResult AdicionarAcompanhamento(int ProjetoId)
        {
            ViewBag.ProjetoId = ProjetoId;
            return View();
        }

        [HttpGet]
        public IActionResult EditarAcompanhamento(int id)
        {
            var acompanhamento = _acompanhamentoProjetoRepository.Get(id);

            if (acompanhamento == null) return NotFound();

            return View(acompanhamento);
        }

        [HttpGet]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public IActionResult ApagarAcompanhamento(int id)
        {
            var acompanhamento = _acompanhamentoProjetoRepository.Get(id);

            if (acompanhamento == null) return NotFound();

            return View(acompanhamento);
        }

        [HttpPost]
        public IActionResult EditarAcompanhamento(AcompanhamentoProjeto acompanhamento)
        {
            EditarAcompanhamentoProjeto(acompanhamento);
            Notify(title: "OK!", message: "Acompanhamento Editado!", notificationType: UINotificationType.success);
            return RedirectToAction("Edit", "Projetos", new { id = acompanhamento.ProjetoId });
        }

        [HttpPost]
        public IActionResult AdicionarAcompanhamento(AcompanhamentoProjeto acompanhamento, int ProjetoId)
        {
            AdicionarAcompanhamentoProjeto(
                acompanhamento.Titulo, 
                acompanhamento.DescricaoAcompanhamento, 
                ProjetoId, 
                TipoAcompanhamento.Default,
                SubTipoAcompanhamento.NA);

            Notify(title: "OK!", message: "Acompanhamento adicionado!", notificationType: UINotificationType.success);
            return RedirectToAction("Edit", "Projetos", new { id = ProjetoId });
        }

        [HttpPost, ActionName("ApagarAcompanhamento")]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public IActionResult ApagarAcompanhamentoConfirmed(int id)
        {
            var acompanhamento = _acompanhamentoProjetoRepository.Get(id);
            ApagarAcompanhamentoProjeto(acompanhamento);
            Notify(title: "OK!", message: "Acompanhamento Apagado!", notificationType: UINotificationType.success);
            return RedirectToAction("Edit", "Projetos", new { id = acompanhamento.ProjetoId });
        }

        [HttpGet]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public IActionResult ApagarAnexo(int id)
        {
            var anexo = _anexoProjetoRepository.Get(id);

            if (anexo == null) return NotFound();

            return View(anexo);
        }

        [HttpPost, ActionName("ApagarAnexo")]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public IActionResult ApagarAnexoConfirmed(int id)
        {
            var anexo = _anexoProjetoRepository.Get(id);
            ApagarAnexoProjeto(anexo);
            Notify(title: "OK!", message: "Anexo Apagado!", notificationType: UINotificationType.success);
            return RedirectToAction("Edit", "Projetos", new { id = anexo.ProjetoId });
        }

        [HttpGet]
        public IActionResult CreateStakeholder(int Projetoid, string from)
        {
            ViewBag.ProjetoId = Projetoid;
            ViewBag.From = from;
            return View();
        }

        [HttpPost]
        public IActionResult CreateStakeholder(StakeholderProjeto stakeholderProjeto, string from)
        {
            try
            {
                if (string.IsNullOrEmpty(stakeholderProjeto.Nome) || string.IsNullOrEmpty(stakeholderProjeto.Email))
                {
                    Notify(title: "Ops!", message: "Algo deu errado!", notificationType: UINotificationType.warning);
                    return RedirectToAction(nameof(CreateStakeholder), new { Projetoid = stakeholderProjeto.ProjetoId, from });
                }

                _stakeholderProjetoRepository.Add(stakeholderProjeto);
                _stakeholderProjetoRepository.SaveChanges();

                _logger.LogWarning($"{HttpContext.Request.Method}/ - criou stakeholder {stakeholderProjeto.Id} no projeto {stakeholderProjeto.ProjetoId}");

                Notify(title: "OK!", message: "Stakeholder adicionado!", notificationType: UINotificationType.success);

                if (from == "SegundaEtapa")
                    return RedirectToAction("CreateSegundaEtapa", new { idProjeto = stakeholderProjeto.ProjetoId });
                else
                    return RedirectToAction("Edit", new { id = stakeholderProjeto.ProjetoId });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado ao adicionar um novo stakeholder");
                Notify(title: "Ops!", message: "Algo deu errado!", notificationType: UINotificationType.warning);
                return RedirectToAction(nameof(CreateStakeholder), new { Projetoid = stakeholderProjeto.ProjetoId, from });
            }
        }

        [HttpGet]
        public IActionResult EditStakeholder(int id, int Projetoid, string from)
        {
            var stakeholder = _stakeholderProjetoRepository.Get(id);
            ViewBag.ProjetoId = Projetoid;
            ViewBag.From = from;
            return View(stakeholder);
        }

        [HttpPost]
        public IActionResult EditStakeholder(StakeholderProjeto stakeholderProjeto, string from)
        {
            try
            {
                _stakeholderProjetoRepository.Update(stakeholderProjeto);
                _stakeholderProjetoRepository.SaveChanges();
                _logger.LogWarning($"{HttpContext.Request.Method}/ - editou stakeholder {stakeholderProjeto.Id} no projeto {stakeholderProjeto.ProjetoId}");
                Notify(title: "OK!", message: "Stakeholder editado!", notificationType: UINotificationType.success);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado ao editar o stakeholder");
                Notify(title: "Ops!", message: "Algo deu errado!", notificationType: UINotificationType.warning);
            }

            if (from == "SegundaEtapa")
                return RedirectToAction("CreateSegundaEtapa", new { idProjeto = stakeholderProjeto.ProjetoId });
            else
                return RedirectToAction("Edit", new { id = stakeholderProjeto.ProjetoId });
        }

        [HttpGet]
        public IActionResult DeleteStakeholder(int id, int Projetoid, string from)
        {
            var stakeholder = _stakeholderProjetoRepository.Get(id);
            ViewBag.ProjetoId = Projetoid;
            ViewBag.From = from;
            return View(stakeholder);
        }

        [HttpPost, ActionName("DeleteStakeHolder")]
        public IActionResult DeleteStakeholderConfirmed(StakeholderProjeto stakeholderProjeto, string from)
        {
            try
            {
                _stakeholderProjetoRepository.Remove(stakeholderProjeto);
                _stakeholderProjetoRepository.SaveChanges();
                _logger.LogWarning($"{HttpContext.Request.Method}/ - apagou stakeholder {stakeholderProjeto.Id} no projeto {stakeholderProjeto.ProjetoId}");
                Notify(title: "OK!", message: "Stakeholder excluído!", notificationType: UINotificationType.success);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado ao excluir o stakeholder");
                Notify(title: "Ops!", message: "Algo deu errado!", notificationType: UINotificationType.warning);
            }

            if (from == "SegundaEtapa")
                return RedirectToAction("CreateSegundaEtapa", new { idProjeto = stakeholderProjeto.ProjetoId });
            else
                return RedirectToAction("Edit", new { id = stakeholderProjeto.ProjetoId });
        }

        [HttpGet]
        public IActionResult CreateVolumetriaProjeto(int Projetoid, string from)
        {
            ViewBag.ProjetoId = Projetoid;
            ViewBag.From = from;
            return View();
        }

        [HttpGet]
        public IActionResult CreateRestricaoProjeto(int Projetoid, string from)
        {
            ViewBag.ProjetoId = Projetoid;
            ViewBag.From = from;
            return View();
        }

        [HttpPost]
        public IActionResult CreateRestricaoProjeto(RestricoesProjeto restricoesProjetoProjeto, string from)
        {
            try
            {
                _restricoesProjetoProjetoRepository.Add(restricoesProjetoProjeto);
                _restricoesProjetoProjetoRepository.SaveChanges();
                _logger.LogWarning($"{HttpContext.Request.Method}/ - criou restrição {restricoesProjetoProjeto.Id} no projeto {restricoesProjetoProjeto.ProjetoId}");
                Notify(title: "OK!", message: "Restrição adicionada!", notificationType: UINotificationType.success);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado ao adicionar uma nova restrição");
                Notify(title: "Ops!", message: "Algo deu errado!", notificationType: UINotificationType.warning);
            }

            if (from == "SegundaEtapa")
                return RedirectToAction("CreateSegundaEtapa", new { idProjeto = restricoesProjetoProjeto.ProjetoId });
            else
                return RedirectToAction("Edit", new { id = restricoesProjetoProjeto.ProjetoId });
        }

        [HttpGet]
        public IActionResult EditRestricaoProjeto(int id, int Projetoid, string from)
        {
            var restricao = _restricoesProjetoProjetoRepository.Get(id);
            ViewBag.ProjetoId = Projetoid;
            ViewBag.From = from;
            return View(restricao);
        }

        [HttpPost]
        public IActionResult EditRestricaoProjeto(RestricoesProjeto restricoesProjetoProjeto, string from)
        {
            try
            {
                _restricoesProjetoProjetoRepository.Update(restricoesProjetoProjeto);
                _restricoesProjetoProjetoRepository.SaveChanges();
                _logger.LogWarning($"{HttpContext.Request.Method}/ - editou restrição {restricoesProjetoProjeto.Id} no projeto {restricoesProjetoProjeto.ProjetoId}");
                Notify(title: "OK!", message: "Restrição editada!", notificationType: UINotificationType.success);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado ao editar a restrição");
                Notify(title: "Ops!", message: "Algo deu errado!", notificationType: UINotificationType.warning);
            }

            if (from == "SegundaEtapa")
                return RedirectToAction("CreateSegundaEtapa", new { idProjeto = restricoesProjetoProjeto.ProjetoId });
            else
                return RedirectToAction("Edit", new { id = restricoesProjetoProjeto.ProjetoId });
        }

        [HttpGet]
        public IActionResult DeleteRestricaoProjeto(int id, int Projetoid, string from)
        {
            var restricao = _restricoesProjetoProjetoRepository.Get(id);
            ViewBag.ProjetoId = Projetoid;
            ViewBag.From = from;
            return View(restricao);
        }

        [HttpPost, ActionName("DeleteRestricaoProjeto")]
        public IActionResult DeleteRestricaoProjetoConfirmed(RestricoesProjeto restricoesProjetoProjeto, string from)
        {
            try
            {
                _restricoesProjetoProjetoRepository.Remove(restricoesProjetoProjeto);
                _restricoesProjetoProjetoRepository.SaveChanges();
                _logger.LogWarning($"{HttpContext.Request.Method}/ - excluiu restrição {restricoesProjetoProjeto.Id} no projeto {restricoesProjetoProjeto.ProjetoId}");
                Notify(title: "OK!", message: "Restrição excluída!", notificationType: UINotificationType.success);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado ao excluir a restrição");
                Notify(title: "Ops!", message: "Algo deu errado!", notificationType: UINotificationType.warning);
            }

            if (from == "SegundaEtapa")
                return RedirectToAction("CreateSegundaEtapa", new { idProjeto = restricoesProjetoProjeto.ProjetoId });
            else
                return RedirectToAction("Edit", new { id = restricoesProjetoProjeto.ProjetoId });
        }

        [HttpGet]
        public IActionResult CreateRelacaoProjeto(int Projetoid, string from)
        {
            ViewBag.ProjetoId = Projetoid;
            ViewBag.From = from;
            return View();
        }

        [HttpPost]
        public IActionResult CreateRelacaoProjeto(RelacoesProjeto relacoesProjetoProjeto, string from)
        {
            try
            {
                _relacoesProjetoProjetoRepository.Add(relacoesProjetoProjeto);
                _relacoesProjetoProjetoRepository.SaveChanges();
                _logger.LogWarning($"{HttpContext.Request.Method}/ - criou relação {relacoesProjetoProjeto.Id} no projeto {relacoesProjetoProjeto.ProjetoId}");
                Notify(title: "OK!", message: "Relação adicionada!", notificationType: UINotificationType.success);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado ao adicionar uma nova relação");
                Notify(title: "Ops!", message: "Algo deu errado!", notificationType: UINotificationType.warning);
            }

            if (from == "SegundaEtapa")
                return RedirectToAction("CreateSegundaEtapa", new { idProjeto = relacoesProjetoProjeto.ProjetoId });
            else
                return RedirectToAction("Edit", new { id = relacoesProjetoProjeto.ProjetoId });
        }

        [HttpGet]
        public IActionResult EditRelacaoProjeto(int id, int Projetoid, string from)
        {
            var relacao = _relacoesProjetoProjetoRepository.Get(id);
            ViewBag.ProjetoId = Projetoid;
            ViewBag.From = from;
            return View(relacao);
        }

        [HttpPost]
        public IActionResult EditRelacaoProjeto(RelacoesProjeto relacoesProjetoProjeto, string from)
        {
            try
            {
                _relacoesProjetoProjetoRepository.Update(relacoesProjetoProjeto);
                _relacoesProjetoProjetoRepository.SaveChanges();
                _logger.LogWarning($"{HttpContext.Request.Method}/ - editou relação {relacoesProjetoProjeto.Id} no projeto {relacoesProjetoProjeto.ProjetoId}");
                Notify(title: "OK!", message: "Relação editada!", notificationType: UINotificationType.success);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado ao editar a relação");
                Notify(title: "Ops!", message: "Algo deu errado!", notificationType: UINotificationType.warning);
            }

            if (from == "SegundaEtapa")
                return RedirectToAction("CreateSegundaEtapa", new { idProjeto = relacoesProjetoProjeto.ProjetoId });
            else
                return RedirectToAction("Edit", new { id = relacoesProjetoProjeto.ProjetoId });
        }

        [HttpGet]
        public IActionResult DeleteRelacaoProjeto(int id, int Projetoid, string from)
        {
            var relacao = _relacoesProjetoProjetoRepository.Get(id);
            ViewBag.ProjetoId = Projetoid;
            ViewBag.From = from;
            return View(relacao);
        }

        [HttpPost, ActionName("DeleteRelacaoProjeto")]
        public IActionResult DeleteRelacaoProjetoConfirmed(RelacoesProjeto relacoesProjetoProjeto, string from)
        {
            try
            {
                _relacoesProjetoProjetoRepository.Remove(relacoesProjetoProjeto);
                _relacoesProjetoProjetoRepository.SaveChanges();
                _logger.LogWarning($"{HttpContext.Request.Method}/ - excluiu relação {relacoesProjetoProjeto.Id} no projeto {relacoesProjetoProjeto.ProjetoId}");
                Notify(title: "OK!", message: "Relação excluída!", notificationType: UINotificationType.success);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado ao excluir a relação");
                Notify(title: "Ops!", message: "Algo deu errado!", notificationType: UINotificationType.warning);
            }

            if (from == "SegundaEtapa")
                return RedirectToAction("CreateSegundaEtapa", new { idProjeto = relacoesProjetoProjeto.ProjetoId });
            else
                return RedirectToAction("Edit", new { id = relacoesProjetoProjeto.ProjetoId });
        }

        [HttpGet]
        public IActionResult CreateAliadoProjeto(int Projetoid, string from)
        {
            ViewBag.ProjetoId = Projetoid;
            ViewBag.From = from;
            return View();
        }

        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public IActionResult CreateEntregaProjeto(int projetoId)
        {
            ViewBag.ProjetoId = projetoId;
            _ProjetosService.ConfigViewBag(ViewBag);
            return View();
        }

        [HttpPost]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public IActionResult CreateEntregaProjeto(EntregaProjeto entrega, int projetoId)
        {
            try
            {
                _entregasProjetosRepository.Add(entrega);
                _entregasProjetosRepository.SaveChanges();

                AdicionarAcompanhamentoProjeto(
                    "Criação de Entrega", 
                    "Uma nova entrega foi adicionada no projeto", 
                    projetoId, 
                    TipoAcompanhamento.Automatic,
                    SubTipoAcompanhamento.Requirement_Added);

                var controleEtapa = new ControleEtapasProjetos()
                {
                    EtapaId = entrega.EtapaProjetoId,
                    Fim = null,
                    TipoProjeto = TipoProjeto.EntregaEstruturante,
                    ProjetoId = entrega.Id
                };
                _controleEtapasProjetosService.AddStep(controleEtapa);

                _logger.LogWarning($"{HttpContext.Request.Method}/ - adicionou entrega {entrega.Id} no projeto {entrega.ProjetoId}");
                Notify("Entrega adicionada com sucesso", "Ok!", UINotificationType.success);
                return RedirectToAction("Edit", "Projetos", new { id = entrega.ProjetoId });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado quando adicionava uma nova entrega na Projeto {projetoId}", entrega.ProjetoId);
                Notify("Atenção!", "Algo deu errado ao cadastrar a entrega!", UINotificationType.warning);
                return RedirectToAction("CreateEntregaProjeto", "Projetos", new { projetoId = entrega.ProjetoId });
            }
        }

        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public IActionResult EditarEntregaProjeto(int entregaId, int projetoId)
        {
            var entrega = _entregasProjetosRepository.Get(entregaId);
            if (entrega == null) return NotFound();
            ViewBag.ProjetoId = projetoId;
            _ProjetosService.ConfigViewBag(ViewBag);
            return View(entrega);
        }

        [HttpPost]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public async Task<IActionResult> EditarEntregaProjeto(EntregaProjeto entrega)
        {
            try
            {
                _entregasProjetosRepository.Update(entrega);

                var mudouEtapa = _commonService.CheckIfFieldHasBeenModified("EntregaProjeto", "EtapaProjetoId");

                var user = GetActiveDirectoryUserInformation();

                var projeto = _ProjetosRepository.Get(entrega.ProjetoId);

                projeto.DataAtualizacao = DateTime.Now;

                if (mudouEtapa)
                {
                    var lastStates = new string[]
                    {
                        "Concluído",
                        "Cancelado",
                        "Transferido para PPs"
                    };

                    var nomeEtapa = await _ProjetosService.PegarNomeEtapaProjeto(entrega.EtapaProjetoId);

                    AdicionarAcompanhamentoProjeto(
                        "Mudança de Etapa da Entrega",
                        $"{user.Username} mudou status da entrega {entrega.NomeEntrega} para '{nomeEtapa}'",
                        projeto.Id,
                        TipoAcompanhamento.Automatic,
                        SubTipoAcompanhamento.Status_Changed);

                    _logger.LogWarning($"{HttpContext.Request.Method}/ mudou status da entrega {entrega.Id} para '{nomeEtapa}'.");

                    var isLastState = lastStates.Contains(nomeEtapa);
                    DateTime? endDate = isLastState ? DateTime.Now : null;

                    // Finaliza Etapa Anterior e Adiciona nova etapa
                    var finishedLastStep = _controleEtapasProjetosService.FinishLastStep(entrega.Id, TipoProjeto.EntregaEstruturante);
                    if (finishedLastStep)
                    {
                        var controleEtapa = new ControleEtapasProjetos()
                        {
                            EtapaId = entrega.EtapaProjetoId,
                            Fim = endDate,
                            TipoProjeto = TipoProjeto.EntregaEstruturante,
                            ProjetoId = entrega.Id
                        };
                        _controleEtapasProjetosService.AddStep(controleEtapa);
                    }

                    if (nomeEtapa == "Concluído")
                    {
                        entrega.DataConclusao = DateTime.Now;
                        entrega.IsInRollback = false;
                    }
                }

                _ProjetosRepository.SaveChanges();

                _entregasProjetosRepository.SaveChanges();

                _logger.LogWarning($"{HttpContext.Request.Method}/ atualizou entrega {entrega.Id} no projeto {entrega.ProjetoId}");
                Notify("Entrega atualizada com sucesso", "Ok!", UINotificationType.success);
                return RedirectToAction("Edit", "Projetos", new { id = entrega.ProjetoId });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado quando atualizava a entrega na Projeto {projetoId}", entrega.ProjetoId);
                Notify("Atenção!", "Algo deu errado ao atualizar a entrega!", UINotificationType.warning);
                return RedirectToAction("EditarEntregaProjeto", "Projetos", new { id = entrega.Id, projetoId = entrega.ProjetoId });
            }
        }

        [HttpGet]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public async Task<IActionResult> ConcluirEntregaProjeto(int entregaId, int ProjetoId)
        {
            try
            {
                await _ProjetosService.ConcluirEntregaProjeto(entregaId);
                _logger.LogWarning($"{HttpContext.Request.Method}/ - concluiu a entrega {entregaId} no projeto {ProjetoId}");
                Notify("Entrega concluída com sucesso", "Ok!", UINotificationType.success);
                return RedirectToAction("Edit", "Projetos", new { id = ProjetoId });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado quando concluia a entrega na Projeto {ProjetoId}", ProjetoId);
                Notify("Atenção!", "Algo deu errado ao concluir a entrega!", UINotificationType.warning);
                return RedirectToAction("Edit", "Projetos", new { id = ProjetoId });
            }
        }

        [HttpGet]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public IActionResult DeleteEntregaProjeto(int entregaId)
        {
            var entrega = _entregasProjetosRepository.Get(entregaId);
            if (entrega == null) return NotFound();
            return View(entrega);
        }

        [HttpPost]
        [ActionName("DeleteEntregaProjeto")]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public IActionResult DeleteEntregaProjetoConfirmed(int id)
        {
            var entrega = _entregasProjetosRepository.Get(id);
            try
            {
                if (entrega == null) return NotFound();
                _entregasProjetosRepository.Remove(entrega);
                _entregasProjetosRepository.SaveChanges();
                _logger.LogWarning($"{HttpContext.Request.Method}/ - excluiu entrega {entrega.Id} no projeto {entrega.ProjetoId}");
                Notify("Entrega excluída com sucesso", "Ok!", UINotificationType.success);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado quando excluia a entrega na Projeto {ProjetoId}", entrega.ProjetoId);
                Notify("Atenção!", "Algo deu errado ao excluir a entrega!", UINotificationType.warning);
            }
            return RedirectToAction("Edit", "Projetos", new { id = entrega.ProjetoId });
        }

        [HttpPost]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public IActionResult AdicionarAcompanhamentoAjax(int? id, string followup)
        {
            try
            {
                if (id == null || string.IsNullOrEmpty(followup))
                    return Json(new { status = 400, message = "Id ou mensagem vazia" });

                AdicionarAcompanhamentoProjeto("Follow Up", followup, id.Value, TipoAcompanhamento.Default, SubTipoAcompanhamento.NA);

                return Json(new { status = 200, message = "Acompanhamento adicionado!" });
            }
            catch (Exception)
            {
                return Json(new { status = 500, message = "Algo deu errado ao adicionar o acompanhamento!" });
            }
        }

        [HttpPost]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public async Task<IActionResult> ConcluirProjeto(int? id)
        {
            try
            {
                if (id == null)
                    return Json(new { status = 400, message = "Id vazio" });

                var etapaId = await _ProjetosService.PegarEtapaIdProjeto("Concluído");
                var projeto = _ProjetosRepository.Get(id.Value);
                projeto.EtapaProjetoId = etapaId;
                _ProjetosRepository.Update(projeto);
                _ProjetosRepository.SaveChanges();

                return Json(new { status = 200, message = "Projeto Concluído!" });
            }
            catch (Exception)
            {
                return Json(new { status = 500, message = "Algo deu errado ao concluir o projeto!" });
            }
        }

        [HttpGet]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public async Task<IActionResult> RollbackProjeto(int? id)
        {
            if (!id.HasValue)
                return NotFound();

            ViewBag.Id = id.Value;

            var entregas = await _entregasProjetosRepository.GetEntregasByProjectId(id.Value);
            ViewBag.Entregas = new SelectList(entregas, "Id", "NomeEntrega");

            _rollbackService.ConfigViewbag(ViewBag);

            return View();
        }

        [HttpPost, ActionName("RollbackProjeto")]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public async Task<IActionResult> RollbackProjetoPost(RollbackDTO rollbackDTO, int[] causasIds, int[] sistemasIds, int[] areasIds)
        {
            try
            {
                if (rollbackDTO.ProjetoId == 0)
                {
                    Notify(message: "Algo deu errado ao iniciar o rollback!", title: "Atenção!", UINotificationType.warning);
                    ViewBag.Id = rollbackDTO.ProjetoId;
                    var entregas = await _entregasProjetosRepository.GetEntregasByProjectId(rollbackDTO.ProjetoId);
                    ViewBag.Entregas = new SelectList(entregas, "Id", "NomeEntrega");
                    _rollbackService.ConfigViewbag(ViewBag);
                    return View(rollbackDTO);
                }

                if (causasIds.Length <= 0 || sistemasIds.Length <= 0)
                {
                    Notify(message: "preencha as causas e sistemas!", title: "Atenção!", UINotificationType.warning);
                    ViewBag.Id = rollbackDTO.ProjetoId;
                    var entregas = await _entregasProjetosRepository.GetEntregasByProjectId(rollbackDTO.ProjetoId);
                    ViewBag.Entregas = new SelectList(entregas, "Id", "NomeEntrega");
                    _rollbackService.ConfigViewbag(ViewBag);
                    return View(rollbackDTO);
                }

                if (string.IsNullOrEmpty(rollbackDTO.Impacto))
                {
                    Notify(message: "preencha o impacto!", title: "Atenção!", UINotificationType.warning);
                    ViewBag.Id = rollbackDTO.ProjetoId;
                    var entregas = await _entregasProjetosRepository.GetEntregasByProjectId(rollbackDTO.ProjetoId);
                    ViewBag.Entregas = new SelectList(entregas, "Id", "NomeEntrega");
                    _rollbackService.ConfigViewbag(ViewBag);
                    return View(rollbackDTO);
                }

                if (rollbackDTO.AfetaProjetoTodo == false && (rollbackDTO.EntregaId.HasValue == false || rollbackDTO.EntregaId.Value == 0))
                {
                    Notify(message: "selecione uma entrega!", title: "Atenção!", UINotificationType.warning);
                    ViewBag.Id = rollbackDTO.ProjetoId;
                    var entregas = await _entregasProjetosRepository.GetEntregasByProjectId(rollbackDTO.ProjetoId);
                    ViewBag.Entregas = new SelectList(entregas, "Id", "NomeEntrega");
                    _rollbackService.ConfigViewbag(ViewBag);
                    return View(rollbackDTO);
                }

                var projeto = _ProjetosRepository.Get(rollbackDTO.ProjetoId);
                projeto.IsInRollback = rollbackDTO.AfetaProjetoTodo;
                _ProjetosRepository.Update(projeto);
                _ProjetosRepository.SaveChanges();

                if (rollbackDTO.AfetaProjetoTodo == false && rollbackDTO.EntregaId.HasValue)
                {
                    await _entregasProjetosRepository.SetEntregaInRollbackById(rollbackDTO.EntregaId.Value);
                }

                Rollback rollback = new()
                {
                    AutorId = GetCurrentUserId(),
                    Impacto = rollbackDTO.Impacto,
                    TipoProjeto = TipoProjeto.Estruturante,
                    ProjetoId = rollbackDTO.ProjetoId,
                    PossuiCusto = rollbackDTO.PossuiCusto,
                    CausasIds = [.. causasIds],
                    SistemasEnvolvidos = [.. sistemasIds],
                    ProximosPassos = rollbackDTO.ProximosPassos,
                    AreasAfetadas = [.. areasIds],
                    Inicio = DateTime.Now,
                    EntregaId = rollbackDTO.EntregaId,
                    AfetaProjetoTodo = rollbackDTO.AfetaProjetoTodo,
                    InicioRollback = null,
                    FimRollback = null,
                    UsuariosImpactados = null,
                    BytesDocumentacaoTecnica = null,
                    NomeArquivoDocumentacaoTecnica = null,
                    ContentTypeDocumentacaoTecnica = null
                };

                _rollbackService.IniciarRollback(rollback);

                string rollbackDescription = $"Rollback Iniciado. " + Environment.NewLine + rollback.Impacto;

                AdicionarAcompanhamentoProjeto(
                    "Rollback Iniciado", 
                    rollbackDescription, 
                    rollbackDTO.ProjetoId, 
                    TipoAcompanhamento.Rollback,
                    SubTipoAcompanhamento.NA);

                Notify(message: "Rollback Iniciado", title: "Ok!", UINotificationType.success);

                _logger.LogWarning($"{HttpContext.Request.Method}/ - iniciou rollback {rollback.Id} no projeto {rollbackDTO.ProjetoId}");

                return RedirectToAction("Edit", "Projetos", new { id = rollbackDTO.ProjetoId });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado quando iniciava o rollback do projeto {ProjetoId}", rollbackDTO.ProjetoId);
                Notify(message: "Algo deu errado ao iniciar o rollback!", title: "Atenção!", UINotificationType.warning);
                _rollbackService.ConfigViewbag(ViewBag);
                return View(rollbackDTO);
            }
        }

        [HttpGet]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public async Task<IActionResult> EncerrarRollbackProjeto(int? rollbackId, int? projectId)
        {
            try
            {
                if (rollbackId == null)
                {
                    Notify(message: "Algo deu errado ao encerrar o rollback do projeto!", title: "Atenção!", UINotificationType.warning);
                    return RedirectToAction("Edit", "Projetos", new { id = projectId });
                }

                var rollback = _rollbackService.GetRollbackById(rollbackId.Value);

                if (rollback == null)
                {
                    Notify(message: "Algo deu errado ao encerrar o rollback do projeto!", title: "Atenção!", UINotificationType.warning);
                    return RedirectToAction("Edit", "Projetos", new { id = projectId });
                }

                var projeto = _ProjetosRepository.Get(rollback.ProjetoId);

                if (rollback.AfetaProjetoTodo)
                {
                    projeto.IsInRollback = false;
                    _ProjetosRepository.Update(projeto);
                    _ProjetosRepository.SaveChanges();
                }

                _rollbackService.EncerrarRollback(rollback.Id);

                if (rollback.EntregaId.HasValue && rollback.EntregaId.Value != 0)
                {
                    await _entregasProjetosRepository.RemoveRollbackFromEntregaById(rollback.EntregaId.Value);
                    var entrega = _entregasProjetosRepository.Get(rollback.EntregaId.Value);

                    AdicionarAcompanhamentoProjeto(
                        "Rollback Encerrado", 
                        $"Rollback encerrado referente a entrega: '{entrega.NomeEntrega}'.", 
                        rollback.ProjetoId, 
                        TipoAcompanhamento.Rollback,
                        SubTipoAcompanhamento.NA);
                }
                else
                {
                    AdicionarAcompanhamentoProjeto(
                        "Rollback Encerrado", 
                        "ROLLBACK ENCERRADO", 
                        rollback.ProjetoId, 
                        TipoAcompanhamento.Rollback,
                        SubTipoAcompanhamento.NA);
                }

                _logger.LogWarning($"{HttpContext.Request.Method}/ - encerrou rollback {rollback.Id} no projeto {rollback.ProjetoId}");
                Notify(message: "Rollback Encerrado", title: "Ok!", UINotificationType.success);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado quando encerrava o rollback do projeto {projectId}", projectId);
                Notify(message: "Algo deu errado ao encerrar o rollback do projeto!", title: "Atenção!", UINotificationType.warning);
            }

            return RedirectToAction("Edit", "Projetos", new { id = projectId });
        }

        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public async Task<IActionResult> TransferirProjeto(int projetoId)
        {
            try
            {
                var ppCriadaId = await _ProjetosService.TransferirProjeto(projetoId, GetCurrentUserId());

                if (ppCriadaId != 0)
                {
                    _logger.LogWarning($"{HttpContext.Request.Method}/ - transferiu projeto {projetoId} para PPs");

                    return Json(new
                    {
                        status = 200,
                        message = "Projeto transferido com sucesso!",
                        id = ppCriadaId
                    });
                }

                return Json(new { status = 500, message = "Algo deu errado ao transferir o projeto!" });
            }
            catch (Exception)
            {
                return Json(new { status = 500, message = "Algo deu errado ao transferir o projeto!" });
            }
        }

        [HttpGet]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public async Task<IActionResult> EditRollback(int? id)
        {
            if (!id.HasValue)
                return NotFound();

            var rollback = _rollbackService.GetRollbackById(id.Value);

            _rollbackService.ConfigViewbagForEdit(ViewBag, rollback);

            var entregas = await _entregasProjetosRepository.GetEntregasByProjectId(rollback.ProjetoId);
            ViewBag.Entregas = new SelectList(entregas, "Id", "NomeEntrega");
            ViewBag.EntregaId = rollback.EntregaId ?? 0;

            return View(rollback);
        }

        [HttpPost]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public async Task<IActionResult> EditRollback(
            Rollback rollback, 
            int[] causasIds, 
            int[] sistemasIds, 
            int[] areasIds, 
            int entregaAtualId,
            IFormFile? postedFile)
        {
            try
            {
                if (ModelState.IsValid)
                {
                    rollback.CausasIds = [.. causasIds];
                    rollback.SistemasEnvolvidos = [.. sistemasIds];
                    rollback.AreasAfetadas = [.. areasIds];

                    if (postedFile != null)
                    {
                        byte[] bytes;
                        using (BinaryReader binaryReader = new(postedFile.OpenReadStream()))
                        {
                            bytes = binaryReader.ReadBytes((int)postedFile.Length);
                        }

                        rollback.NomeArquivoDocumentacaoTecnica = postedFile.FileName;
                        rollback.ContentTypeDocumentacaoTecnica = postedFile.ContentType;
                        rollback.BytesDocumentacaoTecnica = bytes;
                    }

                    if (rollback.AfetaProjetoTodo == false && (rollback.EntregaId.HasValue == false || rollback.EntregaId.Value == 0))
                    {
                        var entregas = await _entregasProjetosRepository.GetEntregasByProjectId(rollback.ProjetoId);
                        ViewBag.Entregas = new SelectList(entregas, "Id", "NomeEntrega");
                        ViewBag.EntregaId = entregaAtualId;
                        Notify($"selecione uma entrega!", title: "Atenção!", notificationType: UINotificationType.warning);
                        _rollbackService.ConfigViewbagForEdit(ViewBag, rollback);
                        return View(rollback);
                    }

                    var projeto = _ProjetosRepository.Get(rollback.ProjetoId);
                    projeto.IsInRollback = rollback.AfetaProjetoTodo;
                    _ProjetosRepository.Update(projeto);
                    _ProjetosRepository.SaveChanges();

                    if (rollback.EntregaId.HasValue && rollback.EntregaId.Value != 0)
                    {
                        var mesmaEntrega = (entregaAtualId == rollback.EntregaId);

                        if (mesmaEntrega == false)
                        {
                            if (entregaAtualId != 0)
                                await _entregasProjetosRepository.RemoveRollbackFromEntregaById(entregaAtualId);

                            await _entregasProjetosRepository.SetEntregaInRollbackById(rollback.EntregaId.Value);
                        }
                    }

                    _rollbackService.UpdateRollback(rollback);
                    Notify(title: "Sucesso!", message: "Rollback atualizado.", notificationType: UINotificationType.success);
                    _logger.LogWarning($"{HttpContext.Request.Method}/ - editou rollback {rollback.Id} no projeto {rollback.ProjetoId}");
                    return RedirectToAction(nameof(Edit), new { id = rollback.ProjetoId });
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado quando tentava editar o rollback {rollbackId}.", rollback.Id);
            }

            var entregas2 = await _entregasProjetosRepository.GetEntregasByProjectId(rollback.ProjetoId);
            ViewBag.Entregas = new SelectList(entregas2, "Id", "NomeEntrega");
            ViewBag.EntregaId = entregaAtualId;
            Notify($"Algo deu errado!", title: "Atenção!", notificationType: UINotificationType.warning);
            _rollbackService.ConfigViewbagForEdit(ViewBag, rollback);
            return View(rollback);
        }

        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public IActionResult AdicionarArquivoBiWeekly(IFormFile postedFile, int mes, int semana, int projetoId)
        {
            try
            {
                if (postedFile == null)
                {
                    Notify("Um arquivo deve ser anexado!");
                    return RedirectToAction("Edit", "Projetos", new { id = projetoId });
                }

                var contentType = postedFile.ContentType;

                var acceptedFormats = new List<string>([
                        "image/jpeg", "image/gif", "image/png", "image/webp", "image/bmp"
                ]);

                if (!acceptedFormats.Contains(contentType))
                {
                    Notify("Só serão permitidos imagens!");
                    return RedirectToAction("Edit", "Projetos", new { id = projetoId });
                }

                byte[] bytes;
                using (BinaryReader binaryReader = new BinaryReader(postedFile.OpenReadStream()))
                {
                    bytes = binaryReader.ReadBytes((int)postedFile.Length);
                }

                var name = Path.GetFileName(postedFile.FileName);

                var arquivo = new ArquivoBiWeekly
                {
                    AutorId = GetCurrentUserId(),
                    ProjetoId = projetoId,
                    TipoProjeto = TipoProjeto.Estruturante,
                    Mes = mes,
                    Semana = semana,
                    NomeArquivo = name,
                    ContentType = contentType,
                    Bytes = bytes
                };

                _arquivosBiWeeklyService.AdicionarArquivo(arquivo);

                _logger.LogWarning($"{HttpContext.Request.Method}/ - adicionou arquivo bi-weekly {arquivo.Id} no projeto {arquivo.ProjetoId}");

                Notify("Arquivo adicionado com sucesso", "Sucesso!", UINotificationType.success);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado quando adicionou um novo aquivo bi-weekly no projeto {projetoId}", projetoId);
                Notify("Atenção!", "Algo deu errado ao salvar o arquivo!", UINotificationType.warning);
            }

            return RedirectToAction("Edit", "Projetos", new { id = projetoId });
        }

        [HttpPost]
        public IActionResult GetImageBiWeekly(int? fileId)
        {
            try
            {
                if (fileId == null)
                    return Json(new { status = 400, message = "Id vazio" });

                var file = _arquivosBiWeeklyService.GetById(fileId.Value);

                if (file == null)
                    return Json(new { status = 404, message = "Arquivo não encontrado" });

                var dataImageBytes = $"data:{file.ContentType};base64, {Convert.ToBase64String(file.Bytes)}";

                _logger.LogWarning($"{HttpContext.Request.Method}/ visualizou arquivo bi-weekly {file.Id} no projeto {file.ProjetoId}");

                return Json(new { status = 200, message = "OK", data = dataImageBytes });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado quando o usuário acessava um arquivo bi-weekly no projeto");
                return Json(new { status = 500, message = "Algo deu errado ao acessar arquivo" });
            }
        }

        [HttpPost]
        [Authorize(Policy = PolicyNameConstants.ESTRUTURANTE_ADMIN)]
        public IActionResult RemoveImageBiWeekly(int? fileId)
        {
            try
            {
                if (fileId == null)
                    return Json(new { status = 400, message = "Id vazio" });

                _arquivosBiWeeklyService.DesativarArquivo(fileId.Value);

                _logger.LogWarning($"{HttpContext.Request.Method}/ - desativou arquivo bi-weekly {fileId}");

                return Json(new { status = 200, message = "Arquivo Desativado" });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Algo deu errado quando o usuário iria desativar um arquivo bi-weekly no projeto.");
                return Json(new { status = 500, message = "Algo deu errado ao desativar arquivo" });
            }
        }

        [HttpPost]
        public IActionResult VisualizarPreviaArquivo(int id)
        {
            try
            {
                var acompanhamento = _anexoProjetoRepository.Get(id);
                if (acompanhamento == null || acompanhamento.Bytes == null)
                    return Json(new { status = 404, message = "Arquivo não encontrado." });

                var arquivo = _visualizarArquivosService.VisualizarArquivo(acompanhamento.Bytes, acompanhamento.ContentType);

                return Json(new { status = 200, data = arquivo });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao acessar o arquivo selecionado");
                return Json(new { status = 500, message = "Erro ao acessar o arquivo selecionado" });
            }
        }

        [HttpPost]
        public IActionResult GetUserFromActiveDirectory(string email)
        {
            try
            {
                var user = _userService.GetUserFromAD(ADSearchType.Email, email);

                if (user == null)
                    return Json(new { status = 404, message = "Usuário não encontrado!" });

                return Json(new { status = 200, message = "Usuário encontrado!", user });
            }
            catch (Exception)
            {
                return Json(new { status = 500, message = "Algo deu errado ao procurar o usuário!" });
            }
        }
    }
}

import java.io.*;

/**
 * Diagnóstico avançado para encontrar R no Windows
 */
public class TesteRAvancado {

    public static void main(String[] args) {
        System.out.println("🔍 DIAGNÓSTICO AVANÇADO R - WINDOWS");
        System.out.println("===================================\n");

        // 1. Verificar PATH do Java
        verificarPATH();

        // 2. Testar caminhos específicos do Windows
        testarCaminhosWindows();

        // 3. Tentar encontrar R automaticamente
        procurarRAutomaticamente();

        // 4. Testar variáveis de ambiente
        verificarVariaveisAmbiente();

        System.out.println("\n===================================");
        System.out.println("🏁 DIAGNÓSTICO AVANÇADO CONCLUÍDO");
    }

    private static void verificarPATH() {
        System.out.println("1️⃣ VERIFICANDO PATH DO JAVA");
        System.out.println("---------------------------");

        String path = System.getenv("PATH");
        if (path != null) {
            System.out.println("📂 PATH completo:");
            String[] paths = path.split(";");
            for (int i = 0; i < paths.length; i++) {
                if (paths[i].toLowerCase().contains("r\\") || paths[i].toLowerCase().contains("r-")) {
                    System.out.println("🎯 [" + i + "] " + paths[i] + " ← POSSÍVEL R");
                } else {
                    System.out.println("   [" + i + "] " + paths[i]);
                }
            }
        } else {
            System.out.println("❌ PATH não encontrado");
        }
        System.out.println();
    }

    private static void testarCaminhosWindows() {
        System.out.println("2️⃣ TESTANDO CAMINHOS ESPECÍFICOS WINDOWS");
        System.out.println("----------------------------------------");

        String[] caminhosPossiveis = {
                "C:\\Program Files\\R\\R-4.5.1\\bin\\Rscript.exe",
                "C:\\Program Files\\R\\R-4.5.1\\bin\\R.exe",
                "C:\\Program Files (x86)\\R\\R-4.5.1\\bin\\Rscript.exe",
                "C:\\Program Files (x86)\\R\\R-4.5.1\\bin\\R.exe",
                "C:\\R\\R-4.5.1\\bin\\Rscript.exe",
                "C:\\R\\R-4.5.1\\bin\\R.exe"
        };

        for (String caminho : caminhosPossiveis) {
            File arquivo = new File(caminho);
            if (arquivo.exists()) {
                System.out.println("✅ ENCONTRADO: " + caminho);
                testarComando(caminho, "--version");
            } else {
                System.out.println("❌ Não existe: " + caminho);
            }
        }
        System.out.println();
    }

    private static void procurarRAutomaticamente() {
        System.out.println("3️⃣ PROCURANDO R AUTOMATICAMENTE");
        System.out.println("------------------------------");

        // Procurar em Program Files
        procurarEmDiretorio("C:\\Program Files");
        procurarEmDiretorio("C:\\Program Files (x86)");
        procurarEmDiretorio("C:\\");

        System.out.println();
    }

    private static void procurarEmDiretorio(String basePath) {
        try {
            File baseDir = new File(basePath);
            if (!baseDir.exists()) return;

            File[] dirs = baseDir.listFiles(File::isDirectory);
            if (dirs == null) return;

            for (File dir : dirs) {
                if (dir.getName().startsWith("R") || dir.getName().toLowerCase().contains("r-")) {
                    System.out.println("🔍 Verificando: " + dir.getAbsolutePath());

                    // Procurar por bin/Rscript.exe
                    File binDir = new File(dir, "bin");
                    if (binDir.exists()) {
                        File rscript = new File(binDir, "Rscript.exe");
                        File r = new File(binDir, "R.exe");

                        if (rscript.exists()) {
                            System.out.println("✅ RSCRIPT ENCONTRADO: " + rscript.getAbsolutePath());
                            testarComando(rscript.getAbsolutePath(), "--version");
                        }

                        if (r.exists()) {
                            System.out.println("✅ R ENCONTRADO: " + r.getAbsolutePath());
                            testarComando(r.getAbsolutePath(), "--version");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao procurar em " + basePath + ": " + e.getMessage());
        }
    }

    private static void testarComando(String comando, String parametro) {
        try {
            ProcessBuilder pb = new ProcessBuilder(comando, parametro);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String linha;
                while ((linha = reader.readLine()) != null) {
                    output.append(linha).append("\n");
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("   ✅ FUNCIONA! Saída: " + output.toString().split("\n")[0]);
            } else {
                System.out.println("   ❌ Falhou com código: " + exitCode);
            }

        } catch (Exception e) {
            System.out.println("   ❌ Erro: " + e.getMessage());
        }
    }

    private static void verificarVariaveisAmbiente() {
        System.out.println("4️⃣ VERIFICANDO VARIÁVEIS DE AMBIENTE");
        System.out.println("-----------------------------------");

        String[] variaveis = {"R_HOME", "R_USER", "R_LIBS_USER", "PATH"};

        for (String var : variaveis) {
            String valor = System.getenv(var);
            if (valor != null) {
                if (var.equals("PATH")) {
                    System.out.println("📂 " + var + ": [muito longo - já mostrado acima]");
                } else {
                    System.out.println("📂 " + var + ": " + valor);
                }
            } else {
                System.out.println("❌ " + var + ": não definida");
            }
        }

        // Verificar se ProcessBuilder consegue encontrar Rscript
        System.out.println("\n🔍 Teste ProcessBuilder (como Java faz):");
        String[] comandos = {"Rscript", "R"};

        for (String cmd : comandos) {
            try {
                ProcessBuilder pb = new ProcessBuilder(cmd, "--version");
                System.out.println("✅ Java consegue encontrar: " + cmd);
                Process process = pb.start();
                int exitCode = process.waitFor();
                System.out.println("   Código de saída: " + exitCode);
            } catch (IOException e) {
                System.out.println("❌ Java NÃO consegue encontrar: " + cmd);
                System.out.println("   Erro: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("❌ Erro geral com " + cmd + ": " + e.getMessage());
            }
        }
    }
}
package controller;

import java.io.BufferedReader;

import java.io.InputStreamReader;

public class RedesController {
	
	public RedesController() {
		super();
	}

	private String os() {
		String os = System.getProperty("os.name");
		
		if (os.contains("Windows")) {
			return "Windows";
		} else if (os.contains("Linux")) {
			return "Linux";
		} else if (os.contains("Mac")) {
			return "Mac";
		} else {
			return "Outro";
		}

	}
	
	public void mostraros() {
		String sistemaOperacional = os();
		System.out.println("Sistema operacional identificado: " + sistemaOperacional);
	}
	
	public void ip() {
        String sistemaOperacional = os();
        String comando;

        if (sistemaOperacional.equals("Windows")) {
            comando = "ipconfig";
        } else if (sistemaOperacional.equals("Linux") || sistemaOperacional.equals("Mac")) {
            comando = "ifconfig";
        } else {
            System.out.println("Sistema operacional não suportado para este comando.");
            return;
        }
        
        callProcess(comando);
    }

    /**
     * Executa o processo e trata a saída, exibindo o nome do adaptador e o IPv4.
     */
    @SuppressWarnings("deprecation")
    private void callProcess(String proc) {
        try {
            Process process = Runtime.getRuntime().exec(proc);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "UTF-8"));

            String sistemaOperacional = os();
            String line;
            String adapterName = null; // Guarda o nome do adaptador atual

            while ((line = reader.readLine()) != null) {
                // ----- Lógica para Windows (em português e inglês) -----
                if (sistemaOperacional.equals("Windows")) {
                    // Linha de adaptador: pode conter "Adaptador" (ex.: "Adaptador de Rede Ethernet:")
                    if (line.toLowerCase().contains("adaptador")) {
                        // Pega a parte antes dos dois pontos para obter o nome
                        adapterName = line.split(":")[0].trim();
                    }
                    // Linha com o IPv4: procura "endereço ipv4" ou "ipv4 address"
                    else if (line.toLowerCase().contains("endereço ipv4") ||
                             line.toLowerCase().contains("ipv4 address")) {
                        String[] parts = line.split(":");
                        if (parts.length > 1) {
                            String ip = parts[1].trim();
                            if (adapterName != null && !ip.isEmpty()) {
                                System.out.println(adapterName + " -> " + ip);
                            }
                        }
                    }
                }
                // ----- Lógica para Linux / Mac -----
                else {
                    // Linha de adaptador: a linha que não inicia com espaço e contém ":"
                    if (!line.startsWith(" ") && line.contains(":")) {
                        adapterName = line.split(":")[0].trim();
                    }
                    
                    // Linha com IPv4: começa com "inet "
                    if (line.trim().startsWith("inet ")) {
                        String[] parts = line.trim().split("\\s+");
                        if (parts.length > 1) {
                            String ip = parts[1];
                            // Ignora o loopback 127.0.0.1
                            if (!ip.equals("127.0.0.1") && adapterName != null) {
                                System.out.println(adapterName + " -> " + ip);
                            }
                        }
                    }
                }
            }

            reader.close();
        } catch (Exception e) {
            String msg = e.getMessage();
            System.err.println(msg);
        }
    }
}
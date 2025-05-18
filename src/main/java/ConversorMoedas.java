import java.util.Scanner;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ConversorMoedas {

    // === COLOQUE SUA API KEY AQUI ===
    // Substitua o valor abaixo pela sua chave real que você recebeu por e-mail
    private static final String API_KEY = "5cf8a79659f555ab3616f4f3";

    // Opções de conversão: cada par é [moeda origem, moeda destino]
    private static final String[][] OPCOES = {
            {"USD", "BRL"},
            {"BRL", "USD"},
            {"EUR", "BRL"},
            {"BRL", "EUR"},
            {"USD", "EUR"},
            {"EUR", "USD"}
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Conversor de Moedas ===");
        for (int i = 0; i < OPCOES.length; i++) {
            System.out.printf("%d - %s para %s\n", i + 1, OPCOES[i][0], OPCOES[i][1]);
        }
        System.out.print("Escolha uma opção: ");
        int opcao = scanner.nextInt();

        if (opcao < 1 || opcao > OPCOES.length) {
            System.out.println("Opção inválida. Encerrando.");
            scanner.close();
            return;
        }

        System.out.print("Digite o valor a converter: ");
        double valor = scanner.nextDouble();

        String moedaOrigem = OPCOES[opcao - 1][0];
        String moedaDestino = OPCOES[opcao - 1][1];

        try {
            double taxa = obterTaxaCambio(moedaOrigem, moedaDestino);
            double convertido = valor * taxa;
            System.out.printf("%.2f %s equivale a %.2f %s\n", valor, moedaOrigem, convertido, moedaDestino);
        } catch (Exception e) {
            System.out.println("Erro ao obter cotação: " + e.getMessage());
        }

        scanner.close();
    }

    private static double obterTaxaCambio(String de, String para) throws Exception {
        String urlStr = String.format(
                "https://v6.exchangerate-api.com/v6/%s/pair/%s/%s",
                API_KEY, de, para
        );

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder resposta = new StringBuilder();
        String linha;
        while ((linha = reader.readLine()) != null) {
            resposta.append(linha);
        }
        reader.close();

        JsonObject json = JsonParser.parseString(resposta.toString()).getAsJsonObject();

        String resultado = json.get("result").getAsString();
        if (!"success".equalsIgnoreCase(resultado)) {
            throw new Exception("Falha na API: " + json.get("error-type").getAsString());
        }

        return json.get("conversion_rate").getAsDouble();
    }
}

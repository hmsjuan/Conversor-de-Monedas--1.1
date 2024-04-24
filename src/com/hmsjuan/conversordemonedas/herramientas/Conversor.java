package com.hmsjuan.conversordemonedas.herramientas;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Conversor {
    private String monedaLocal;
    private Map<String, Double> tasasDeCambio;



    public Conversor(String monedaLocal) {
        this.monedaLocal = monedaLocal;
        this.tasasDeCambio = new HashMap<>();

        Currency monedas = new Currency();
        monedas.llenarMonedas();

    }



    public void obtenerTasasDeCambio() throws IOException {
        String urlStr = "https://api.exchangerate-api.com/v4/latest/" + monedaLocal;
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == 200) {
            InputStream responseBody = connection.getInputStream();
            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(responseBodyReader);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject rates = jsonObject.getAsJsonObject("rates");

            for (Map.Entry<String, JsonElement> entry : rates.entrySet()) {
                String moneda = entry.getKey();
                Double tasa = entry.getValue().getAsDouble();
                tasasDeCambio.put(moneda, tasa);
            }

            connection.disconnect();
        } else {
            System.out.println("Error al obtener las tasas de cambio.");
        }
    }

    public double convertir(String monedaOrigen, String monedaDestino, double cantidad) {
        double tasaOrigen = tasasDeCambio.getOrDefault(monedaOrigen, 1.0);
        double tasaDestino = tasasDeCambio.getOrDefault(monedaDestino, 1.0);
        return (cantidad / tasaOrigen) * tasaDestino;
    }


}
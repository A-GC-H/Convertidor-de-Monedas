
package com.example.currencyconverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class CurrencyConverterApp {

    // Reemplaza YOUR_API_KEY con tu clave de API real de ExchangeRate-API.com
    private static final String API_KEY = "9c4776dcd9d813da069efd64"; // <-- ¡IMPORTANTE: Reemplaza esto!
    private static final String API_BASE_URL = "https://v6.exchangerate-api.com/v6/";

    private final HttpClient httpClient;
    private final Gson gson;

    public CurrencyConverterApp() {
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        this.gson = new GsonBuilder().setPrettyPrinting().create(); // Pretty printing para mejor legibilidad del JSON
    }

    /**
     * Obtiene las tasas de conversión para una moneda base.
     * @param baseCurrency El código de la moneda base (ej. "USD", "EUR").
     * @return Un objeto ExchangeRateResponse que contiene las tasas de conversión.
     * @throws IOException Si ocurre un error de red o I/O.
     * @throws InterruptedException Si la operación HTTP es interrumpida.
     * @throws RuntimeException Si la respuesta de la API no es exitosa o hay un problema con la clave de API.
     */
    public ExchangeRateResponse getExchangeRates(String baseCurrency) throws IOException, InterruptedException {
        String url = API_BASE_URL + API_KEY + "/latest/" + baseCurrency.toUpperCase();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ExchangeRateResponse rateResponse = gson.fromJson(response.body(), ExchangeRateResponse.class);
            if ("success".equalsIgnoreCase(rateResponse.getResult())) {
                return rateResponse;
            } else {
                // La API puede devolver un "result" diferente a "success" en caso de error.
                // Aquí podrías añadir más manejo de errores basado en los mensajes de la API.
                throw new RuntimeException("Error al obtener las tasas de cambio de la API: " + rateResponse.getResult() + ". Revisa tu clave de API y la moneda base.");
            }
        } else {
            // Manejo de errores HTTP
            System.err.println("Error HTTP al obtener las tasas de cambio: " + response.statusCode());
            System.err.println("Cuerpo de la respuesta: " + response.body());
            throw new RuntimeException("Fallo en la petición HTTP: Código " + response.statusCode());
        }
    }

    /**
     * Convierte una cantidad de una moneda a otra.
     * @param amount La cantidad a convertir.
     * @param fromCurrency El código de la moneda de origen (ej. "USD").
     * @param toCurrency El código de la moneda de destino (ej. "EUR").
     * @return La cantidad convertida.
     * @throws IOException Si ocurre un error de red o I/O.
     * @throws InterruptedException Si la operación HTTP es interrumpida.
     * @throws IllegalArgumentException Si la moneda de destino no es encontrada en las tasas de cambio.
     */
    public double convertCurrency(double amount, String fromCurrency, String toCurrency)
            throws IOException, InterruptedException, IllegalArgumentException {

        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return amount; // No hay conversión si son la misma moneda
        }

        ExchangeRateResponse rates = getExchangeRates(fromCurrency);
        Map<String, Double> conversionRates = rates.getConversionRates();

        if (conversionRates == null || !conversionRates.containsKey(toCurrency.toUpperCase())) {
            throw new IllegalArgumentException("La moneda de destino '" + toCurrency + "' no se encontró en las tasas de cambio para " + fromCurrency + ".");
        }

        double rate = conversionRates.get(toCurrency.toUpperCase());
        return amount * rate;
    }

    public static void main(String[] args) {
        // !!! Asegúrate de reemplazar "TU_CLAVE_API_AQUI" en la constante API_KEY arriba !!!
        if (API_KEY.equals("TU_CLAVE_API_AQUI")) {
            System.err.println("ERROR: Por favor, reemplaza 'TU_CLAVE_API_AQUI' con tu clave de API real de ExchangeRate-API.com.");
            System.err.println("Puedes obtener una clave gratuita en: https://www.exchangerate-api.com/.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        CurrencyConverterApp converter = new CurrencyConverterApp();

        System.out.println("--- Conversor de Monedas ---");
        System.out.println("Monedas soportadas (ejemplos): USD, EUR, GBP, JPY, MXN, CAD, AUD, BRL, ARS, COP, CLP...");

        while (true) {
            try {
                System.out.print("\nIngrese la cantidad a convertir: ");
                double amount = scanner.nextDouble();

                System.out.print("Ingrese el código de la moneda de origen (ej. USD): ");
                String fromCurrency = scanner.next().toUpperCase();

                System.out.print("Ingrese el código de la moneda de destino (ej. EUR): ");
                String toCurrency = scanner.next().toUpperCase();

                double convertedAmount = converter.convertCurrency(amount, fromCurrency, toCurrency);
                System.out.printf("%.2f %s son %.2f %s%n", amount, fromCurrency, convertedAmount, toCurrency);

            } catch (InputMismatchException e) {
                System.err.println("Error: Cantidad inválida. Por favor, ingrese un número.");
                scanner.next(); // Limpiar el buffer del scanner
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
            } catch (RuntimeException e) {
                System.err.println("Error de la API o del sistema: " + e.getMessage());
                // Considera si quieres salir o intentar de nuevo en caso de errores de API graves.
            } catch (IOException e) {
                System.err.println("Error de I/O o de red: " + e.getMessage());
            } catch (InterruptedException e) {
                System.err.println("La operación fue interrumpida: " + e.getMessage());
                Thread.currentThread().interrupt(); // Restablecer el estado de interrupción
                break; // Salir del bucle si la operación fue interrumpida
            } finally {
                System.out.print("¿Desea realizar otra conversión? (s/n): ");
                String choice = scanner.next();
                if (!choice.equalsIgnoreCase("s")) {
                    break;
                }
            }
        }
        scanner.close();
        System.out.println("¡Gracias por usar el conversor de monedas!");
    }
}
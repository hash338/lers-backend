package lb.lers.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String model;

    private final WebClient webClient = WebClient.create();
    private final ObjectMapper mapper = new ObjectMapper();

    public AiResult summarizeIncident(String type,
                                      String location,
                                      String notes) {
        try {
            String prompt = String.format("""
                Emergency incident in Lebanon.
                Type: %s | Location: %s | Notes: %s
                Generate:
                1. A 1-2 sentence summary for the first responder.
                2. Risk level: LOW | MEDIUM | HIGH | CRITICAL
                Respond ONLY in JSON format (no markdown):
                {"summary":"...","riskLevel":"HIGH"}
                """, type, location, notes != null ? notes : "none");

            String response = callGemini(prompt);
            JsonNode node = mapper.readTree(response);
            return new AiResult(
                    node.get("summary").asText(),
                    node.get("riskLevel").asText()
            );
        } catch (Exception e) {
            System.out.println("Gemini unavailable: " + e.getMessage());
            return new AiResult("AI summary unavailable.", "HIGH");
        }
    }

    private String callGemini(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + model + ":generateContent?key=" + apiKey;

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                )),
                "generationConfig", Map.of(
                        "temperature", 0.3,
                        "thinkingConfig", Map.of("thinkingBudget", 0)
                )
        );

        String response = webClient.post()
                .uri(url)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode root = mapper.readTree(response);
            JsonNode parts = root.path("candidates").get(0)
                    .path("content").path("parts");

            // Find text part (skip thinking parts)
            String text = "";
            for (JsonNode part : parts) {
                if (part.has("text") && !part.has("thought")) {
                    text = part.path("text").asText();
                    break;
                }
            }
            if (text.isEmpty()) {
                text = parts.get(0).path("text").asText();
            }

            text = text.replace("```json", "").replace("```", "").trim();
            return text;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response: " + e.getMessage());
        }
    }

    public String suggestDispatch(String incidentType, String location, String summary, String availableTeam) {
        String prompt = String.format("""
        You are an emergency dispatch AI for the Lebanese Civil Defense (LERS system).
        
        Incident:
        - Type: %s
        - Location: %s
        - Summary: %s
        
        Available team:
        %s
        
        Suggest the best team to dispatch. Respond ONLY in JSON (no markdown):
        {"recommendedDriver":"full name or null","recommendedParamedic":"full name or null","recommendedHelper":"full name or null","recommendedVehicle":"vehicle code or null","reasoning":"1-2 sentences","urgency":"CRITICAL or HIGH or MEDIUM"}
        """, incidentType, location, summary, availableTeam);

        return callGemini(prompt);
    }

    public static class AiResult {
        public final String summary;
        public final String riskLevel;
        public AiResult(String summary, String riskLevel) {
            this.summary = summary;
            this.riskLevel = riskLevel;
        }
    }

    public String nationalSummary(String statsJson) {
        String prompt = String.format("""
        You are an emergency management AI for Lebanon (LERS system).
        
        Current national situation:
        %s
        
        Provide a brief national situation summary for the Super Admin.
        Include: active threats, highest risk areas, and recommendations.
        Keep it under 3 sentences, professional tone.
        Respond in English only, no markdown, plain text.
        """, statsJson);
        try {
            return callGemini(prompt);
        } catch (Exception e) {
            return "System operational. All centers on standby.";
        }
    }

    public String reverseGeocode(double latitude, double longitude) {
        String prompt = String.format("""
        Given these GPS coordinates in Lebanon:
        Latitude: %s
        Longitude: %s
        
        Return the street name, area, and city in Lebanon.
        Respond ONLY in JSON (no markdown):
        {"street":"...","area":"...","city":"...","fullAddress":"..."}
        """, latitude, longitude);
        try {
            return callGemini(prompt);
        } catch (Exception e) {
            return "{\"fullAddress\":\"Location at " + latitude + "," + longitude + "\"}";
        }
    }

    public String riskMap(String incidentsJson) {
        String prompt = String.format("""
You are an emergency risk analysis AI for Lebanon (LERS system).

Here are the recent incidents in Lebanon:
%s

You MUST return ALL 6 Lebanese regions regardless of incident data.
Calculate risk percentages based on incidents. If no incidents for a region, use baseline values.

Return ONLY valid JSON with exactly these 6 keys:
{
  "Beirut": {"fireRisk": 45, "accidentRisk": 70, "drowningRisk": 10},
  "Mount Lebanon": {"fireRisk": 30, "accidentRisk": 55, "drowningRisk": 15},
  "North Lebanon": {"fireRisk": 25, "accidentRisk": 40, "drowningRisk": 20},
  "South Lebanon": {"fireRisk": 35, "accidentRisk": 45, "drowningRisk": 60},
  "Bekaa": {"fireRisk": 50, "accidentRisk": 35, "drowningRisk": 5},
  "Nabatieh": {"fireRisk": 30, "accidentRisk": 40, "drowningRisk": 25}
}
""", incidentsJson);
        try {
            return callGemini(prompt);
        } catch (Exception e) {
            System.out.println("Risk map error: " + e.getMessage());
            return "{\"Beirut\":{\"fireRisk\":30,\"accidentRisk\":50,\"drowningRisk\":20}," +
                    "\"Mount Lebanon\":{\"fireRisk\":25,\"accidentRisk\":45,\"drowningRisk\":15}," +
                    "\"North Lebanon\":{\"fireRisk\":20,\"accidentRisk\":35,\"drowningRisk\":25}," +
                    "\"South Lebanon\":{\"fireRisk\":75,\"accidentRisk\":40,\"drowningRisk\":60}," +
                    "\"Bekaa\":{\"fireRisk\":45,\"accidentRisk\":30,\"drowningRisk\":10}," +
                    "\"Nabatieh\":{\"fireRisk\":30,\"accidentRisk\":35,\"drowningRisk\":30}}";
        }
    }
}
package lb.lers.ai;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class DispatchAiController {

    private final GeminiService geminiService;

    public DispatchAiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/dispatch-suggest")
    public ResponseEntity<Map<String, Object>> suggest(
            @RequestBody Map<String, Object> body) {
        try {
            String availableTeam = String.format(
                    "Drivers: %s\nParamedics: %s\nHelpers: %s\nVehicles: %s",
                    body.getOrDefault("drivers", "none"),
                    body.getOrDefault("paramedics", "none"),
                    body.getOrDefault("helpers", "none"),
                    body.getOrDefault("vehicles", "none")
            );
            String result = geminiService.suggestDispatch(
                    body.get("incidentType").toString(),
                    body.get("location").toString(),
                    body.getOrDefault("summary", "").toString(),
                    availableTeam
            );
            return ResponseEntity.ok(Map.of("suggestion", result));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/national-summary")
    public ResponseEntity<Map<String, Object>> nationalSummary(
            @RequestBody Map<String, Object> body) {
        try {
            String json = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(body);
            String summary = geminiService.nationalSummary(json);
            return ResponseEntity.ok(Map.of("summary", summary));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("summary", "System operational. All centers on standby."));
        }
    }

    @PostMapping("/reverse-geocode")
    public ResponseEntity<Map<String, Object>> reverseGeocode(
            @RequestBody Map<String, Object> body) {
        try {
            double lat = Double.parseDouble(body.get("latitude").toString());
            double lng = Double.parseDouble(body.get("longitude").toString());
            String result = geminiService.reverseGeocode(lat, lng);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/risk-map")
    public ResponseEntity<Map<String, Object>> riskMap(
            @RequestBody Map<String, Object> body) {
        try {
            String incidentsJson = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(body.get("incidents"));
            String result = geminiService.riskMap(incidentsJson);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }
}
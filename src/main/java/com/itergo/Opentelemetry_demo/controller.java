package com.itergo.Opentelemetry_demo;

import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.message.StringMapMessage;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.trace.SpanContext;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class controller {
    
    @GetMapping(
            path = "/test"
    )
    public ResponseEntity<?> TestEndpoint() throws Exception {
        // TRACER________________________________________________
        SpanContext spanctx = OpenTelemetry
            .getTracer("Opentelemetry_demo", "semver:1.0.0")
            .getCurrentSpan()
            .getContext();

        // NOTIZ:
        // ist nur ein Duplikat mit einem anderen Port ;)
        /*
        final String uri = "http://localhost:8090/test";
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        MDC.put("res", result);
        */

        // NOTIZ:
        // kann sein, dass TraceID und SpanID automatisch in den Logger eingefügt werden
        // Opentelemetry kommuniziert anscheinend direkt mit log4j und modifiziert die Logs entsprechend, was mega geil ist.
        MDC.put("trace.id", spanctx.getTraceId().toLowerBase16()); 
        MDC.put("span.id", spanctx.getSpanId().toLowerBase16());
        
        // NOTIZ:
        // nur zum Ausprobieren
        log.info("result");

        //*************************************************************************** 
        // TESTFALL 1: Collector nicht am Laufen
        //      Voraussetzung: 
        //          - Opentelemetry Auto Instrumentation: in dem Ordner '/javaagent'
        //      Vorgehensweise:
        //          - Collector ausschalten (wenn diese docker image vorhanden und ist aktiv)
        //          - mit Maven packagen
        //          - java -javaagent: path/to/opentelemetry-javaagent-all.jar -Dotel.exporter:otlp -jar DieApplikation.jar
        //          - Aufruf des Endpoints
        //****************************************************************************
        /*
            // keine zusätzlichen Codes
            // man sieht dass der javaagent versucht, die Pings regelmäßig zur Überprüfung der Konnektivität zu senden
        */
        //*************************************************************************** 
        // TESTFALL 2: Abbruch einer Applikation während der Ausführung
        //      Voraussetzung:
        //          - gleich wie Testfall 1
        //          - du hast Otlp-collector lokal als Docker Image gespeichert.
        //      Vorgehensweise:
        //          - Collector sollte laufen. (Falls gewunscht, kann ich dir einfach diese Image auf Dockerhub zur Verfügung stellen)
        //          - die Zeile auskommentieren und nochmal mit Maven packagen. (kann eigentlich einfach auf intellij so einstellen, dass Javaagent angebunden wird)
        //          - java -javaagent: path/to/opentelemetry-javaagent-all.jar -Dotel.exporter:otlp -jar DieApplikation.jar
        //          - falls Collector nicht vorhanden ist, kannst du den oberen Kommand mit -Dotel.exporter:logging ersetzen,
        //          - Aufruf des Endpoints
        //          - Manueller Abbruch bei dem einkommenden Request
        //*************************************************************************** 
        /*
        // bitte beim Timeout die Applikation abbrechen, dann weißt du dass keine Trace-Daten an Collector geschickt wurden.
        // Diese Trace-Daten werden nach der unteren Response erst an den Collector übergeben. 
        TimeUnit.MINUTES.sleep(10);
        */
        return ResponseEntity.ok().body("hallo");
    }
}

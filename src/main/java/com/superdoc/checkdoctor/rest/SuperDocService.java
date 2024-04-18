package com.superdoc.checkdoctor.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@AllArgsConstructor
public class SuperDocService {

    @Autowired
    private final WebClient superDocWebClient;

    public Mono<Void> check(String uri) {

        return superDocWebClient.get().uri(uri).retrieve().bodyToMono(String.class).flatMap(response -> {

            try {
                SuperDocResponse superDocResponse = new ObjectMapper().readValue(response, SuperDocResponse.class);

                EarliestSlot earliestSlot = superDocResponse.getCalendar().getEarliestSlot();
                String earliestSlotDateString = earliestSlot.getDate();

                LocalDate earliestSlotDate = LocalDate.parse(earliestSlotDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                if (LocalDate.now().plusDays(3).isAfter(earliestSlotDate)) {
                    System.err.println("AVAILABLE DATE: " + earliestSlotDate + ", Time: " + earliestSlot.getText() + " Time "
                            + earliestSlot.getTime());
                    playSound("C:\\Windows\\Media\\Alarm01.wav");
                } else {
                    System.out.println("Current slot: " + earliestSlotDate + ", Time: " + earliestSlot.getText() + " Time "
                            + earliestSlot.getTime());
                }
            } catch (JsonProcessingException e) {
                log.warn("Could not extra response from Server");
            }
            return Mono.empty();
        }).then();


    }

    private static synchronized void playSound(final String url) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    InputStream bufferedInputStream = new BufferedInputStream(Files.newInputStream(new File(url).toPath()));
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class SuperDocResponse {

        private boolean result;
        private String message;
        private Calendar calendar;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Calendar {

        private EarliestSlot earliestSlot;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class EarliestSlot {

        private String date;

        private String time;

        private String text;
    }
}

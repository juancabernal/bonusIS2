package co.edu.uco.messageservice.controller;

import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import co.edu.uco.messageservice.catalog.Message;
import co.edu.uco.messageservice.catalog.MessageCatalog;

@RestController
@RequestMapping(value = "/messages/api/v1/messages", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageController {

    @GetMapping("/{code}")
    public ResponseEntity<Message> getMessage(@PathVariable String code) {
        var msg = MessageCatalog.get(code);
        if (msg == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(msg); // ✅ AHORA DEVUELVE 200 OK
    }

    @GetMapping
    public ResponseEntity<Map<String, Message>> getAllMessages() {
        return ResponseEntity.ok(MessageCatalog.getAll()); // ✅ también 200 OK
    }

    @PutMapping(value = "/{code}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> upsertMessage(@PathVariable String code, @RequestBody Message message) {
        message.setCode(code);
        MessageCatalog.upsert(message);
        return ResponseEntity.ok(message); // ✅ también 200 OK
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Message> deleteMessage(@PathVariable String code) {
        var removed = MessageCatalog.remove(code);
        if (removed == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(removed); // ✅ también 200 OK
    }
}

package lt.js.ratechanges.controller;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lt.js.ratechanges.model.Change;
import lt.js.ratechanges.service.ChangesService;

@RestController
public class ChangesRestController {

    private final ChangesService service;

    public ChangesRestController(ChangesService service) {
        this.service = service;
    }

    @GetMapping("/changes")
    public Collection<Change> changes(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate forDate) {
        if (forDate == null) {
            throw new IllegalArgumentException("missing 'forDate' parameter");
        }
        Collection<Change> changes = service.getOrderedChanges(forDate);
        if (CollectionUtils.isEmpty(changes)) {
            throw new IllegalArgumentException("no data present for date " + forDate.toString());
        }
        return changes;
    }

}

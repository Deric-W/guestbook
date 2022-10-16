/*
 * Copyright 2014-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guestbook;

import io.github.wimdeblauwe.hsbt.mvc.HtmxResponse;
import io.github.wimdeblauwe.hsbt.mvc.HxRequest;
import jakarta.validation.Valid;

import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A controller to handle web requests to manage {@link GuestbookEntry}s
 *
 * @author Paul Henke
 * @author Oliver Drotbohm
 */
@Controller
class GuestbookController {

    private final GuestbookRepository guestbook;


    GuestbookController(GuestbookRepository guestbook) {

        Assert.notNull(guestbook, "Guestbook must not be null!");
        this.guestbook = guestbook;
    }


    @GetMapping(path = "/")
    String index() {
        return "redirect:/guestbook";
    }


    @GetMapping(path = "/guestbook")
    String guestBook(Model model, @ModelAttribute(binding = false) GuestbookForm form) {

        model.addAttribute("entries", guestbook.findAll());
        model.addAttribute("form", form);

        return "guestbook";
    }


    @PostMapping(path = "/guestbook")
    String addEntry(@Valid @ModelAttribute("form") GuestbookForm form, Errors errors, Model model) {

        if (errors.hasErrors()) {
            return guestBook(model, form);
        }

        guestbook.save(form.toNewEntry());

        return "redirect:/guestbook";
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/guestbook/{entry}")
    String removeEntry(@PathVariable Optional<GuestbookEntry> entry) {

        return entry.map(it -> {

            guestbook.delete(it);
            return "redirect:/guestbook";

        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "/guestbook/edit/{entry}")
    String postEntry(@PathVariable Optional<GuestbookEntry> entry, GuestbookForm form) {

        return entry.map(it -> {
            it.setName(it.getName() + " [EDIT]");
            it.setText(form.getText());
            guestbook.save(it);
            return "redirect:/guestbook";

        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


    @HxRequest
    @PostMapping(path = "/guestbook")
    HtmxResponse addEntry(@Valid GuestbookForm form, Model model) {

        model.addAttribute("entry", guestbook.save(form.toNewEntry()));
        model.addAttribute("index", guestbook.count());

        return new HtmxResponse()
                .addTemplate("guestbook :: entry")
                .addTrigger("eventAddedd");
    }


    @HxRequest
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/guestbook/{entry}")
    HtmxResponse removeEntryHtmx(@PathVariable Optional<GuestbookEntry> entry, Model model) {

        return entry.map(it -> {

            guestbook.delete(it);

            model.addAttribute("entries", guestbook.findAll());

            return new HtmxResponse()
                    .addTemplate("guestbook :: entries");

        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
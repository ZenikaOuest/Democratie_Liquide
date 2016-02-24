package com.zenika.liquid.democracy.api.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.zenika.liquid.democracy.api.exception.MalformedSubjectException;
import com.zenika.liquid.democracy.api.service.SubjectService;

import liquid.democracy.model.Subject;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

	private static final Logger LOG = LoggerFactory.getLogger(SubjectController.class);

	@Autowired
	private SubjectService subjectService;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> addSubject(@Validated @RequestBody Subject s) throws MalformedSubjectException {

		LOG.info("addSubject {} ", s);

		Subject out = subjectService.addSubject(s);

		return ResponseEntity.created(
				ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(out.getUuid()).toUri())
				.build();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/inprogress")
	public ResponseEntity<List<Subject>> getSubjectsInProgress() throws MalformedSubjectException {

		LOG.info("getSubjectsInProgress");

		List<Subject> out = subjectService.getSubjectsInProgress();

		if (out.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(out);
		}

		return ResponseEntity.ok(out);
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Subject is not complete")
	@ExceptionHandler(MalformedSubjectException.class)
	public void malFormedSubjectHandler() {
	}

}
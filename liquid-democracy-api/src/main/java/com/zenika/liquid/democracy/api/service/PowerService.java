package com.zenika.liquid.democracy.api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.zenika.liquid.democracy.api.exception.AddPowerOnNonExistingSubjectException;
import com.zenika.liquid.democracy.api.exception.CloseSubjectException;
import com.zenika.liquid.democracy.api.exception.DeleteNonExistingPowerException;
import com.zenika.liquid.democracy.api.exception.DeletePowerOnNonExistingSubjectException;
import com.zenika.liquid.democracy.api.exception.UserAlreadyGavePowerException;
import com.zenika.liquid.democracy.api.exception.UserAlreadyVoteException;
import com.zenika.liquid.democracy.api.exception.UserGivePowerToHimselfException;
import com.zenika.liquid.democracy.api.persistence.SubjectRepository;
import com.zenika.liquid.democracy.api.util.PowerUtil;
import com.zenika.liquid.democracy.authentication.service.CollaboratorService;
import com.zenika.liquid.democracy.model.Power;
import com.zenika.liquid.democracy.model.Subject;

@Service
@EnableRetry
@Retryable(OptimisticLockingFailureException.class)
public class PowerService {

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private CollaboratorService collaboratorService;

	public void addPowerOnSubject(String subjectUuid, Power power)
			throws AddPowerOnNonExistingSubjectException, UserAlreadyGavePowerException,
			UserGivePowerToHimselfException, CloseSubjectException, UserAlreadyVoteException {

		String userId = collaboratorService.currentUser().getCollaboratorId();

		Optional<Subject> s = subjectRepository.findSubjectByUuid(subjectUuid);
		if (!s.isPresent()) {
			throw new AddPowerOnNonExistingSubjectException();
		}
		PowerUtil.checkPowerForAddition(power, s.get(), userId);

		PowerUtil.preparePower(power, s.get(), userId);

		subjectRepository.save(s.get());

	}

	public void deletePowerOnSubject(String subjectUuid) throws DeletePowerOnNonExistingSubjectException,
			DeleteNonExistingPowerException, CloseSubjectException, UserAlreadyVoteException {

		String userId = collaboratorService.currentUser().getCollaboratorId();

		Optional<Subject> s = subjectRepository.findSubjectByUuid(subjectUuid);
		if (!s.isPresent()) {
			throw new DeletePowerOnNonExistingSubjectException();
		}

		Power power = PowerUtil.checkPowerForDelete(s.get(), userId);

		s.get().removePower(power);

		subjectRepository.save(s.get());

	}
}

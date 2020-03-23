package com.eking.momp.aop;

import com.eking.momp.common.ResourceType;
import com.eking.momp.common.bean.ErrorResponse;
import com.eking.momp.common.exception.*;
import com.eking.momp.common.service.LocaleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.Serializable;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@Autowired
	private LocaleService localeService;

	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse alreadyExists(AlreadyExistsException ex) {
		log.error(ex.getMessage(), ex);
		String message = localeService.getMessage("alreadyExists", ex.getLabel());
		return ErrorResponse.of(message);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse sqlConstraint(SQLIntegrityConstraintViolationException ex) {
		log.error(ex.getMessage(), ex);
		String message = localeService.getMessage("sqlConstraint");
		return ErrorResponse.of(message);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	public ErrorResponse inUsed(InUsedException ex) {
		log.error(ex.getMessage(), ex);
		ResourceType resourceType = ex.getResourceType();
		String dataLabel = localeService.getLabel(resourceType.getLabel());
		Serializable dateName = ex.getDataName();

		ResourceType refResourceType = ex.getRefResourceType();
		String reDataLabel = localeService.getLabel(refResourceType.getLabel());
		Serializable refDataName = ex.getRefResourceName();

		String message = localeService.getMessage("inUsed", dataLabel + "("+ dateName +")",
				reDataLabel + "("+ refDataName +")");
		return ErrorResponse.of(message);
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse business(BusinessException ex) {
		log.error(ex.getMessage(), ex);
		return ErrorResponse.of(ex.getMessage());
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse internalServerError(Exception ex) {
		log.error(ex.getMessage(), ex);
		String message = localeService.getMessage("internalServerError");
		return ErrorResponse.of(message);
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse dataNotFound(ResourceNotFoundException ex) {
		log.error(ex.getMessage(), ex);
		ResourceType resourceType = ex.getResourceType();
		Serializable dataId = ex.getDataId();
		String dataLabel = localeService.getLabel(resourceType.getLabel());
		String message = localeService.getMessage("dataNotFound", dataLabel + "("+ dataId +")");
		return ErrorResponse.of(message);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse apiNotFound(ApiNotFoundException ex) {
		log.error(ex.getMessage(), ex);
		String message = localeService.getMessage("apiNotFound");
		return ErrorResponse.of(message);
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse forbidden(ForbiddenException ex) {
		log.error(ex.getMessage(), ex);
		String message = localeService.getMessage("noPermission");
		return ErrorResponse.of(message);
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse elasticSearchIO(ElasticSearchIOException ex) {
		log.error(ex.getMessage(), ex);
		String message = localeService.getMessage("internalServerError");
		return ErrorResponse.of(message);
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse methodArgumentNotValid(MethodArgumentNotValidException ex) {
		List<ObjectError> errors = ex.getBindingResult().getAllErrors();
		StringBuilder sb = new StringBuilder();
		for (ObjectError error : errors) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(error.getDefaultMessage());
		}
		return ErrorResponse.of(sb.toString());
	}

	@InitBinder
	public void globalInitBinder(WebDataBinder binder) {
		binder.addCustomFormatter(new DateFormatter("yyyy-MM-dd HH:mm:ss"), LocalDateTime.class);
	}
}

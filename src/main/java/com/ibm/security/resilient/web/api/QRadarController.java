package com.ibm.security.resilient.web.api;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.security.resilient.common.AESEncryption;
import com.ibm.security.resilient.dao.ResilientBrokerDAO;
import com.ibm.security.resilient.model.QRadar;

/**
 * Resilient broker API implementation class
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 24th, 2017
 */
@RestController
public class QRadarController {
	/**
	 * logger instance for QRadarController class
	 */
	private static final Logger logger = LoggerFactory.getLogger(QRadarController.class);
	/**
	 * Resilient-Broker repository instance
	 */
	@Autowired
	private ResilientBrokerDAO qRadarService;

	/**
	 * This api fetchs list of QRadar objects from database.
	 * 
	 * @return Collection of QRadar
	 */
	@RequestMapping(value = "/api/qradars", 
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<QRadar>> getQRadars() {
		logger.debug("Started executing GET /api/qradars");
		Collection<QRadar> qRadars = qRadarService.findAll();
		logger.debug("Finished executing GET /api/qradars");
		return new ResponseEntity<>(qRadars, HttpStatus.OK);
	}

	/**
	 * This api returns the QRadar object based on passed id
	 * 
	 * @param id
	 *            Id for searching record from Resilient-Broker db
	 * @return instance of QRadar
	 */
	@RequestMapping(value = "/api/qradars/{id}", 
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<QRadar> getQRadar(@PathVariable("id") Long id) {
		logger.debug("Started executing GET /api/qradars{id} with id:{}", id);
		QRadar qRadar = qRadarService.findOne(id);

		if (qRadar == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		logger.debug("Finished executing GET /api/qradars{id} with id:{}", id);
		return new ResponseEntity<>(qRadar, HttpStatus.OK);
	}

	/**
	 * This api creates a QRadar object in database
	 * 
	 * @param qRadar
	 *            Instance of QRadar for saving in db
	 * @return QRadar instance of QRadar
	 */
	@RequestMapping(value = "/api/qradars", 
			method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<QRadar> createQRadar(@RequestBody QRadar qRadar) {
		logger.debug("Started executing POST /api/qradars with QRadar object: {}", qRadar);
		QRadar savedQRadar = qRadarService.create(qRadar);
		logger.debug("Finishe executing POST /api/qradars with QRadar id :" + qRadar.getId());
		return new ResponseEntity<>(savedQRadar, HttpStatus.CREATED);
	}

	/**
	 * This api update the QRadar object in database based on passed id
	 * 
	 * @param qRadar
	 *            Instance of QRadar for updating in db
	 * @return QRadar instance of QRadar
	 */
	@RequestMapping(value = "/api/qradars/{id}", 
			method = RequestMethod.PUT, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<QRadar> updateQRadar(@RequestBody QRadar qRadar) {
		logger.debug("Started executing PUT /api/qradars with QRadar object: {}", qRadar);
		QRadar updatedQRadar = qRadarService.update(qRadar);

		if (updatedQRadar == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		logger.debug("Finished executing PUT /api/qradars with QRadar id:{}", qRadar.getId());
		return new ResponseEntity<>(updatedQRadar, HttpStatus.OK);
	}

	/**
	 * Commented delete api.
	 * 
	 * @param id
	 *            for deleting record from Resilient-Broker db
	 * @return instance of QRadar
	 */
	@RequestMapping(value = "/api/qradars/{id}", 
			method = RequestMethod.DELETE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<QRadar> deleteQRadar(@PathVariable("id") Long id, @RequestBody QRadar qRadar) {
		logger.debug("Started executing DELETE /api/qradars/{id}: {}", qRadar);
		qRadarService.delete(id);
		logger.debug("Finished executing DELETE /api/qradars/{id}: {}", qRadar);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/**
	 * This api encrypt the passed plainText
	 * 
	 * @param plainText
	 *            plain text for encrytion
	 * @return Encrypted String object
	 */
	@RequestMapping(value = "/api/encrypt/{plainText}", 
			method = RequestMethod.GET)
	public ResponseEntity<String> encryptPlainText(@PathVariable String plainText) {
		logger.debug("Started executing GET /api/encrypt/{plain_text} for plainText: {}", plainText);
		String encryptedText = null;
		
		try {
			AESEncryption aesEncryption = new AESEncryption();
			encryptedText = aesEncryption.encrypt(plainText);
		} catch (Exception e) {
			encryptedText = e.getMessage();
		}
		logger.debug("Finished executing GET /api/encrypt/{plain_text} for plainText: {}", plainText);
		return new ResponseEntity<>(encryptedText, HttpStatus.OK);
	}

	/**
	 * This api decrypt the passed encryptedText
	 * 
	 * @param encryptedText
	 *            encrypted text for decryption
	 * @return Decrypted String object
	 */
	@RequestMapping(value = "/api/decrypt/{encryptedText}", 
			method = RequestMethod.GET)
	public ResponseEntity<String> decryptEncryptedText(@PathVariable String encryptedText) {
		logger.debug("Started executing GET /api/decrypt/{encryptedText} for encrypted_text: {}", encryptedText);
		String decryptedText = null;
		
		try {
			AESEncryption aesEncryption = new AESEncryption();
			decryptedText = aesEncryption.decrypt(encryptedText);
		} catch (Exception e) {
			decryptedText = e.getMessage();
		}
		logger.debug("Finished executing GET /api/decrypt/{encryptedText} for encrypted_text: {}", encryptedText);
		return new ResponseEntity<>(decryptedText, HttpStatus.OK);
	}
}
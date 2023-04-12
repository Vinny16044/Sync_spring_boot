package com.sync.sync.sync;

import javax.swing.JOptionPane;

import com.google.api.services.drive.model.File;
import com.sync.sync.model.ModelRequest;


public class JobValidate {

	ModelRequest modeloRequisicao;

	public ModelRequest Format(String path, String file) {

		String[] substrings = file.split("[-.]");
		String idCliente = substrings[0];
		String nomeObg = substrings[1];
		String dataFile = substrings[2];
		String dataPath = path;
		ModelRequest requisicao;

		if (file.contains(".LOADED.PDF") || file.contains(".FormatoInvalido.PDF") || file.contains(".LOADED.pdf") || file.contains(".FormatoInvalido.pdf")) {
			ModelRequest request = new ModelRequest(false);
			return request;
		}

		if (dataFile.equalsIgnoreCase(dataPath)) {

			String padrao = "\\d{1,8}-[A-Za-z0-9_]+-\\d{1,6}\\.(pdf|PDF)";
			System.out.println(file);

			requisicao = new ModelRequest(idCliente, nomeObg, dataFile, false, file.matches(padrao));

			return requisicao;
		}

		else {

			ModelRequest request = new ModelRequest(false);
			return request;
		}

	}

}

package com.sync.sync.sync;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.util.Date;
import java.util.HashMap;

import com.sync.sync.model.ModelNewRequest;
import com.sync.sync.model.ModelRequest;



public abstract class AbstractScanJob {



	

	protected String prepareString(String str) {
		return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toUpperCase();
	}

	protected ModelNewRequest  newData(String clienteId, String cnpj, String obrigacaoId, String nome,
			String desc, String dataRef, String dataVenc, String valor, String situacaoId, ModelRequest request) {
		


		if(situacaoId == null){

			situacaoId ="E";
		}
		
		
		

		ModelNewRequest newRequest = new ModelNewRequest(
			clienteId,
			obrigacaoId,
			nome,
			desc,
			dataRef,
			dataVenc,
			valor,
			situacaoId
			);




		

		return newRequest;
	}


}

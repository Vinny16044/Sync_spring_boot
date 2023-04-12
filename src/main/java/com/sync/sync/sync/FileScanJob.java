package com.sync.sync.sync;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import com.sync.sync.model.ModelNewRequest;
import com.sync.sync.model.ModelRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class FileScanJob extends AbstractScanJob {
	private final SimpleDateFormat sdf = new SimpleDateFormat("YYYY" + File.separator + "MM");

	private String subPathThisYear = null;
	private String subPathThisMonth = null;
	private String subPathNextMonth = null;
	private String subPathLastMonth = null;

	private PortalServiceClient client = new PortalServiceClient();
	private PortalApiService apiService = new PortalApiService();

	
	

	private boolean isYearContabilElegible(String path) {

		if (subPathThisYear == null) {
			Calendar today = Calendar.getInstance();
			// subPathThisYear = (today.get(Calendar.YEAR)-1) + "\\Cont�bil";
			subPathThisYear = String.valueOf(today.get(Calendar.YEAR));
		}

		return (path.toUpperCase().indexOf(subPathThisYear + File.separator + "CONT�BIL") > -1
				|| path.toUpperCase().indexOf(subPathThisYear + File.separator + "CONTABIL") > -1);
	}

	private boolean isMonthContabilElegible(String path) {

		if (subPathThisMonth == null) {
			Calendar today = Calendar.getInstance();
			subPathThisMonth = sdf.format(today.getTime());
			today.add(Calendar.MONTH, -1);
			subPathLastMonth = sdf.format(today.getTime());
			today.add(Calendar.MONTH, +2);
			subPathNextMonth = sdf.format(today.getTime());
		}

		return ((path.toUpperCase().indexOf(subPathThisMonth + File.separator + "CONT�BIL") > -1
				|| path.toUpperCase().indexOf(subPathLastMonth + File.separator + "CONT�BIL") > -1
				|| path.toUpperCase().indexOf(subPathNextMonth + File.separator + "CONT�BIL") > -1)
				|| (path.toUpperCase().indexOf(subPathThisMonth + File.separator + "CONTABIL") > -1
						|| path.toUpperCase().indexOf(subPathLastMonth + File.separator + "CONTABIL") > -1
						|| path.toUpperCase().indexOf(subPathNextMonth + File.separator + "CONTABIL") > -1));
	}

	private boolean isMonthFolhaElegible(String path) {

		if (subPathThisMonth == null) {
			Calendar today = Calendar.getInstance();
			subPathThisMonth = sdf.format(today.getTime());
			today.add(Calendar.MONTH, -1);
			subPathLastMonth = sdf.format(today.getTime());
			today.add(Calendar.MONTH, +2);
			subPathNextMonth = sdf.format(today.getTime());
		}

		return (path.toUpperCase().indexOf(subPathThisMonth + File.separator + "FOLHA") > -1
				|| path.toUpperCase().indexOf(subPathLastMonth + File.separator + "FOLHA") > -1
				|| path.toUpperCase().indexOf(subPathNextMonth + File.separator + "FOLHA") > -1);
	}

	public void processFile(File file, ModelRequest request ) throws Exception {

		
		String filenames = file.getName();

		System.out.println(filenames);

		if (filenames.endsWith(".PDF") || filenames.endsWith(".pdf")
		
										&& !filenames.endsWith(".LOADED.PDF") && !filenames.endsWith(".INVALIDO.PDF")) {



			String filename = request.getObrigacao_id();

			String clienteId = request.getCliente_id();

			

			String dataBase = parseDatePath(file.getName(),request);


			

			if (dataBase == null || ((String) dataBase).trim().length() == 0) {
				System.err.println("ERROR Data nâo elegível: " + dataBase + " arquivo: " + filename);
				renameInvalidFile(file);
				return;
			}

			// if (filename.startsWith("APUR_ICMS_") || filename.startsWith("APURACAO_ICMS_")) {
			// 	// APUR_ICMS_MMYYYY.PDF
			// 	// System.out.println("Processando arquivo: " + file.getPath());
			// 	HashMap<String, String> data = newData(clienteId, null, "58", "Apura��o do ICMS",
			// 			"Apura��o de ICMS ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
			// 			formatDayVenc(dataBase, 1, 1), "0", "E");
			// 	createObrigacao(data,
			// 			"APURACAO_ICMS_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf", file, request);
			// 	return;
			// }
			// if (filename.startsWith("APUR_IPI_") || filename.startsWith("APURACAO_IPI_")) {
			// 	// APUR_IPI_MMYYYY.PDF
			// 	// System.out.println("Processando arquivo: " + file.getPath());
			// 	HashMap<String, String> data = newData(clienteId, null, "59", "Apura��o do IPI",
			// 			"Apura��o de IPI ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
			// 			formatDayVenc(dataBase, 1, 1), "0", "E");
			// 	createObrigacao(data,
			// 			"APURACAO_IPI_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf", file,request);
			// 	return;
			// }
			// if (filename.startsWith("APUR_PISCOFINS_") || filename.startsWith("APURACAO_PISCOFINS_")) {
			// 	// APUR_PISCOFINS_MMYYYY.PDF
			// 	// System.out.println("Processando arquivo: " + file.getPath());
			// 	HashMap<String, String> data = newData(clienteId, null, "60", "Apura��o do PIS/COFINS",
			// 			"Apura��o de PIS/COFINS ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase),
			// 			dataBase, formatDayVenc(dataBase, 1, 1), "0", "E");
			// 	createObrigacao(data,
			// 			"APURACAO_PISCOFINS_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf",
			// 			file,request);
			// 	return;
			// }

			if (filename.startsWith("BALANCOPATRIMONIAL") || filename.startsWith("BALANCO_PATRIMONIAL")) {
				
					// Dia 30
					// BALANCOPATRIMONIAL_MODELO1_ANUAL_DD_MM_YYYY.PDF
					// BALANCOPATRIMONIAL_MODELO2_ANUAL_DD_MM_YYYY.PDF
					// BALANCOPATRIMONIAL_MODELO3_ANUAL_DD_MM_YYYY.PDF
					// BALANCOPATRIMONIAL_MODELO4_ANUAL_DD_MM_YYYY.PDF
					// BALANCOPATRIMONIAL_MODELO5_ANUAL_DD_MM_YYYY.PDF
					// BALANCOPATRIMONIAL_MODELO6_ANUAL_DD_MM_YYYY.PDF
					// System.out.println("Processando arquivo: " + file.getPath());


					
					//se conecta ao metodo abstrato e retorna um objeto para criar a obrigação
					
					System.out.println(dataBase);
					LocalDateTime data = LocalDateTime.parse(dataBase, DateTimeFormatter.ISO_DATE_TIME);
       			    LocalDateTime novaData = data.plusYears(1);
        			String dataBase1 = novaData.format(DateTimeFormatter.ISO_DATE_TIME);

					ModelNewRequest requestTo = newData(clienteId, null, "1", "Balanco Patrimonial",
							"Balanco Patrimonial Anual - ref. " + formatYearRef(dataBase1),
							formatLastYearDayRef(dataBase1), formatDayVenc(dataBase1, 30, 0), "0", "E",request);
							System.out.println(requestTo.getDataRef());
							

							


					apiService.callCreateObrigacao(file, requestTo, request);
					//client.createObrigacao(file,requestTo,request);
					return;
				
			}

// 			if (filename.startsWith("CONTRATOEXPERIENCIAPRORROGACAO")
// 					|| filename.startsWith("CONTRATO_EXPERIENCIA_PRORROGACAO")) {
// 				// ContratoExperi�nciaProrroga��o.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "49", "Contrato de Experi�ncia e Prorroga��o",
// 						"Contrato de Experi�ncia e Prorroga��o", dataBase, formatDayVenc(dataBase, 10, 0), "0", "E");
// 				createObrigacao(data, "CONTRATO_EXPERIENCIA_PRORROGACAO.pdf", file,request);
// 				return;
// 			}
// 			if (filename.startsWith("DADOSCARTEIRA") || filename.startsWith("DADOS_CARTEIRA")) {
// 				// DadosCarteira.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "50", "Dados da CTPS", "Dados da CTPS",
// 						dataBase, formatDayVenc(dataBase, 10, 0), "0", "E");
// 				createObrigacao(data, "DADOS_CARTEIRA.pdf", file,request);
// 				return;
// 			}

// 			if (filename.startsWith("FICHAREGISTRO") || filename.startsWith("FICHA_REGISTRO")) {
// 				// FichaRegistro.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "51", "Ficha de Registro dos Funcion�rios",
// 						"Ficha de Registro dos Funcion�rios", dataBase, formatDayVenc(dataBase, 10, 0), "0", "E");
// 				createObrigacao(data, "FICHA_REGISTRO.pdf", file,request);
// 				return;
// 			}

// 			if (filename.startsWith("FOLHA DE 13 SALARIO") || filename.startsWith("FOLHA_13o_SALARIO_")) {
// 				// Folha de 13� Sal�rio - 1� Parcela YYYYMM.pdf
// 				// Folha de 13� Sal�rio - 2� Parcela Ativos YYYYMM.pdf
// 				// Folha de 13� Sal�rio - 2� Parcela YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				if (filename.indexOf("2 PARCELA") > -1 || filename.indexOf("_2PARCELA") > -1) {
// 					HashMap<String, String> data = newData(clienteId, null, "53",
// 							"Folha de Pagamento do 13� sal�rio - 2� parcela",
// 							"Folha de Pagamento do 13� sal�rio - 2� parcela", dataBase, formatDayVenc(dataBase, 20, 0),
// 							"0", "E");
// 					createObrigacao(data, "FOLHA_13o_SALARIO_2PARCELA.pdf", file,request);
// 					return;
// 				} else {
// 					HashMap<String, String> data = newData(clienteId, null, "52",
// 							"Folha de Pagamento do 13� sal�rio - 1� parcela",
// 							"Folha de Pagamento do 13� sal�rio - 1� parcela", dataBase, formatDayVenc(dataBase, 20, 0),
// 							"0", "E");
// 					createObrigacao(data, "FOLHA_13o_SALARIO_1PARCELA.pdf", file,request);
// 					return;
// 				}
// 			}

// 			if (filename.startsWith("COMPROVANTEDEVOLUCAOCTPS") || filename.startsWith("COMPROVANTE_DEVOLUCAO_CTPS")) {
// 				// ComprovanteDevolu��oCTPS.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "43", "Comprovante Devolu��o CTPS",
// 						"Comprovante Devolu��o CTPS", dataBase, formatDayVenc(dataBase, 15, 0), "0", "E");
// 				createObrigacao(data, "COMPROVANTE_DEVOLUCAO_CTPS.pdf", file,request);
// 				return;
// 			}

// 			if (filename.startsWith("RECIBODEENTREGACTPS") || filename.startsWith("COMPROVANTE_DEVOLUCAO_CTPS")) {
// 				// RecibodeEntregaCTPS.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "44", "Recibo de Entrega da CTPS",
// 						"Recibo de Entrega da CTPS", dataBase, formatDayVenc(dataBase, 15, 0), "0", "E");
// 				createObrigacao(data, "COMPROVANTE_DEVOLUCAO_CTPS.pdf", file,request);
// 				return;
// 			}

// 			if (filename.startsWith("DARF ADTO 0561 ") || filename.startsWith("DARF_ADIANTAMENTO_0561_")) {
// 				// Dia 20
// 				// DARF ADTO 0561 PERIODO YYYYMMDD.PDF
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "6", "DARF SOBRE ADIANTAMENTO",
// 						"(0561) ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
// 						formatDayVenc(dataBase, 20, 1), "0", "E");
// 				createObrigacao(data,
// 						"DARF_ADIANTAMENTO_0561_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf",
// 						file,request);
// 				return;
// 			}

// 			// Altera��o - 19/06/2018 - Conforme alinhamento em reuni�o - n�o
// 			// subri do phoenix, j� sobe no manual (azure)
// 			// if (filename.startsWith("DARF_COFINS_")) {
// 			// // Dia 20
// 			// // DARF_COFINS_MMYYYY.PDF
// 			// // System.out.println("Processando arquivo: " + file.getPath());
// 			// HashMap<String, String> data = newData(clienteId, null,"3", "DARF
// 			// COFINS", "COFINS (2172) ref. " + formatMonthRef(dataBase) + "/" +
// 			// formatYearRef(dataBase), dataBase, formatDayVenc(dataBase, 20,
// 			// 1), "0");
// 			// createObrigacao(data, "DARF_COFINS_2172_MENSAL_" +
// 			// formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) +
// 			// ".pdf", file);
// 			// return;
// 			// }

// 			if (filename.startsWith("DARF_IPI_5123_") || filename.startsWith("DARF_IPI_5123_MENSAL_")) {
// 				// Dia 20
// 				// DARF_IPI_5123_MMYYYY.PDF
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "6", "DARF IPI",
// 						"IPI (5123) ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
// 						formatDayVenc(dataBase, 20, 1), "0", "E");
// 				createObrigacao(data,
// 						"DARF_IPI_5123_MENSAL_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf",
// 						file,request);
// 				return;
// 			}
// 			// Altera��o - 19/06/2018 - Conforme alinhamento em reuni�o - n�o
// 			// subri do phoenix, j� sobe no manual (azure)
// 			// if (filename.startsWith("DARF_PIS_") ||
// 			// filename.startsWith("DARF_PIS_8109_")) {
// 			// // Dia 25
// 			// // DARF_PIS_MMYYYY.PDF
// 			// // DARF_PIS_8109_MMYYYY.PDF
// 			// // System.out.println("Processando arquivo: " + file.getPath());
// 			// HashMap<String, String> data = newData(clienteId, null,"26", "DARF
// 			// PIS", "PIS (8109) ref. " + formatMonthRef(dataBase) + "/" +
// 			// formatYearRef(dataBase), dataBase, formatDayVenc(dataBase, 25,
// 			// 1), "0");
// 			// createObrigacao(data, "DARF_PIS_8109_MENSAL_" +
// 			// formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) +
// 			// ".pdf", file);
// 			// return;
// 			// }

// 			if (filename.startsWith("DARF 13 SALARIO 0561 PERIODO")
// 					|| filename.startsWith("DARF_0561_13SALARIO_MENSAL_")) {
// 				// Dia 20
// 				// Darf 13 Salario 0561 Periodo YYYYMMDD.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "6", "DARF",
// 						"DARF (0561) ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
// 						formatDayVenc(dataBase, 25, 1), "0", "E");
// 				createObrigacao(data, "DARF_0561_13SALARIO_MENSAL_" + formatMonthRef(dataBase) + "_"
// 						+ formatYearRef(dataBase) + ".pdf", file,request);
// 				return;
// 			}
// 			if (filename.startsWith("DARF PAGTO 0561 PERIODO") || filename.startsWith("DARF_0561_PGTO_MENSAL_")) {
// 				// Dia 20
// 				// Darf Pagto 0561 Periodo YYYYMMDD.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "6", "DARF",
// 						"DARF (0561) ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
// 						formatDayVenc(dataBase, 20, 1), "0", "E");
// 				createObrigacao(data,
// 						"DARF_0561_PGTO_MENSAL_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf",
// 						file,request);
// 				return;
// 			}
// 			if (filename.startsWith("DARF PRO LABORE 0561 PERIODO")
// 					|| filename.startsWith("DARF_0561_PROLABORE_MENSAL_")) {
// 				// Dia 20
// 				// Darf Pro Labore 0561 Periodo YYYYMMDD.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "6", "DARF",
// 						"DARF (0561) ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
// 						formatDayVenc(dataBase, 20, 1), "0", "E");
// 				createObrigacao(data, "DARF_0561_PROLABORE_MENSAL_" + formatMonthRef(dataBase) + "_"
// 						+ formatYearRef(dataBase) + ".pdf", file,request);
// 				return;
// 			}

			if (filename.startsWith("DAS")) {
				// Dia 20
				// DAS_MMYYYY_2255.pdf
				// System.out.println("Processando arquivo: " + file.getPath());

				String valor = "0";

				try {

					PdfParser parser = new PdfParser();
					HashMap<String, String> data = parser.parseDAS(file);

					if (data.containsKey("VALOR")) {
						valor = data.get("VALOR");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				// DAS_SM_MMAAAA_ID - Tipo de obriga��o: Documento de Arrecada��o
				// do Simples Nacional
				String situacaoId = (filename.contains("DAS_") || filename.contains("DAS_SEM_MOVIMENTO_")) ? "S"
						: "E";

						System.out.println(situacaoId+valor);

				ModelNewRequest requestTo = newData(clienteId, null, "7", "DAS - Simples Nacional",
						"DAS - Simples Nacional - ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase),
						dataBase, formatDayVenc(dataBase, 20, 1), valor, situacaoId,request);

						
						
				boolean validateFile = client.validateFile(requestTo,request, file);

				if(validateFile){
					apiService.callCreateObrigacao(file, requestTo, request);
					//client.createObrigacao(file,requestTo,request);
					return;
					
				}
				

			
				

				

			}

			

// 			if (filename.startsWith("ESCRITURACAONOTAS_") || filename.startsWith("ESCRITURACAO_FISCAL_MENSAL_")) {
// 				// Dia 01
// 				// ESCRITURACAONOTAS_MMYYYY.PDF
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "16", "Escritura��o Fiscal",
// 						"Escritura��o Fiscal ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase),
// 						dataBase, formatDayVenc(dataBase, 1, 0), "0", "E");
// 				createObrigacao(data, "ESCRITURACAO_FISCAL_MENSAL_" + formatMonthRef(dataBase) + "_"
// 						+ formatYearRef(dataBase) + ".pdf", file,request);
// 				return;
// 			}

// 			if (filename.startsWith("FOLHA DE ADIANTAMENTO") || filename.startsWith("FOLHA_ADIANTAMENTO_MENSAL_")) {
// 				// Folha de Adiantamento YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "34", "Folha Adiant. Func.",
// 						"Adiantamento - Funcion�rios ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase),
// 						dataBase, formatDayVenc(dataBase, 5, 0), "0", "E");
// 				createObrigacao(data, "FOLHA_ADIANTAMENTO_MENSAL_" + formatMonthRef(dataBase) + "_"
// 						+ formatYearRef(dataBase) + ".pdf", file,request);
// 				return;
// 			}
// 			if (filename.startsWith("FOLHA DE PAGAMENTO ATIVOS") || filename.startsWith("FOLHA_PAGAMENTO_MENSAL_")) {
// 				// Folha de Pagamento Ativos YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "29", "Folha Pgto. Func.",
// 						"Folha de Pgto - Funcion�rios ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase),
// 						dataBase, formatDayVenc(dataBase, 5, 0), "0", "E");
// 				createObrigacao(data,
// 						"FOLHA_PAGAMENTO_MENSAL_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf",
// 						file,request);
// 				return;
// 			}
// 			if (filename.startsWith("FOLHA DE PAGAMENTO") || filename.startsWith("FOLHA_PAGAMENTO_MENSAL_")) {
// 				// Folha de Pagamento YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "29", "Folha Pgto. Func.",
// 						"Folha de Pgto - Funcion�rios ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase),
// 						dataBase, formatDayVenc(dataBase, 5, 0), "0", "E");
// 				createObrigacao(data,
// 						"FOLHA_PAGAMENTO_MENSAL_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf",
// 						file,request);
// 				return;
// 			}
// 			if (filename.startsWith("FOLHA DE PARTICIPACAO NOS LUCROS")
// 					|| filename.startsWith("FOLHA_PARTICIPACAO_LUCROS_")) {
// 				// Folha de Participa��o nos Lucros YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "54", "Folha de Participa��o nos Lucros",
// 						"Folha de Participa��o nos Lucros ref. " + formatMonthRef(dataBase) + "/"
// 								+ formatYearRef(dataBase),
// 						dataBase, formatDayVenc(dataBase, 5, 0), "0", "E");
// 				createObrigacao(data, "FOLHA_PARTICIPACAO_LUCROS_" + formatMonthRef(dataBase) + "_"
// 						+ formatYearRef(dataBase) + ".pdf", file,request);
// 				return;
// 			}
// 			if (filename.startsWith("FOLHA DE PRO LABORE") || filename.startsWith("FOLHA_PROLABORE_MENSAL_")) {
// 				// Folha de Pro Labore YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "28", "Folha Pgto. S�cios",
// 						"Folha de Pgto - S�cios ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase),
// 						dataBase, formatDayVenc(dataBase, 20, 1), "0", "E");
// 				createObrigacao(data,
// 						"FOLHA_PROLABORE_MENSAL_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf",
// 						file,request);
// 				return;
// 			}

// 			if (filename.startsWith("GARE_ICMS_")) {
// 				// GARE_ICMS_MMYYYY.PDF
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "61", "GARE ICMS",
// 						"GARE ICMS ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
// 						formatDayVenc(dataBase, 1, 1), "0", "E");
// 				createObrigacao(data, "GARE_ICMS_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf",
// 						file,request);
// 				return;
// 			}

// 			if (filename.startsWith("GPS 2003 13 SALARIO") || filename.startsWith("GPS_2003_13SALARIO_MENSAL_")) {
// 				// Dia 20
// 				// GPS 2003 13 Salario YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "21", "GPS",
// 						"GPS (2003) ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
// 						formatDayVenc(dataBase, 20, 0), "0", "E");

// 				// Seta data de refer�ncia com -1 m�s para compensar o acr�scimo de 1 m�s da GPS
// 				data.put("DT_REF", formatDayVenc(dataBase, 20, -1));

// 				createObrigacao(data, "GPS_2003_13SALARIO_MENSAL_" + formatMonthRef(dataBase) + "_"
// 						+ formatYearRef(dataBase) + ".pdf", file,request);
// 				return;
// 			}
// 			if (filename.startsWith("GPS 2003 PAGTO") || filename.startsWith("GPS_2003_PGTO_MENSAL_")) {
// 				// Dia 20
// 				// GPS 2003 Pagto YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "21", "GPS",
// 						"GPS (2003) ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
// 						formatDayVenc(dataBase, 20, 1), "0", "E");
// 				createObrigacao(data,
// 						"GPS_2003_PGTO_MENSAL_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf",
// 						file,request);
// 				return;
// 			}
// 			if (filename.startsWith("GPS 2100 13 SALARIO") || filename.startsWith("GPS_2100_13SALARIO_MENSAL_")) {
// 				// Dia 20
// 				// GPS 2100 13 Salario YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "21", "GPS",
// 						"GPS (2100) ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
// 						formatDayVenc(dataBase, 20, 1), "0", "E");
// 				createObrigacao(data, "GPS_2100_13SALARIO_MENSAL_" + formatMonthRef(dataBase) + "_"
// 						+ formatYearRef(dataBase) + ".pdf", file,request);
// 				return;
// 			}
// 			if (filename.startsWith("GPS 2100 PAGTO") || filename.startsWith("GPS_2100_PGTO_MENSAL_")) {
// 				// Dia 20
// 				// GPS 2100 Pagto YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "21", "GPS",
// 						"GPS (2100) ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
// 						formatDayVenc(dataBase, 20, 1), "0", "E");
// 				createObrigacao(data,
// 						"GPS_2100_PGTO_MENSAL_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf",
// 						file,request);
// 				return;
// 			}

// 			if (filename.startsWith("GPS 2208 13 SALARIO") || filename.startsWith("GPS_2208_13SALARIO_MENSAL_")) {
// 				// Dia 20
// 				// GPS 2100 13 Salario YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "21", "GPS",
// 						"GPS (2208) ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
// 						formatDayVenc(dataBase, 20, 1), "0", "E");
// 				createObrigacao(data, "GPS_2208_13SALARIO_MENSAL_" + formatMonthRef(dataBase) + "_"
// 						+ formatYearRef(dataBase) + ".pdf", file,request);
// 				return;
// 			}
// 			if (filename.startsWith("GPS 2208 PAGTO") || filename.startsWith("GPS_2208_PGTO_MENSAL_")) {
// 				// Dia 20
// 				// GPS 2100 Pagto YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "21", "GPS",
// 						"GPS (2208) ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
// 						formatDayVenc(dataBase, 20, 1), "0", "E");
// 				createObrigacao(data,
// 						"GPS_2208_PGTO_MENSAL_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf",
// 						file,request);
// 				return;
// 			}

// 			// Altera��o: 06/06/2018 - Solicita��o Anderson/Ana Paula/Stephanie
// 			// - n�o subri automaticamente o ISS, IRPJ, CSLL e GPS

// 			// if (filename.startsWith("ISS_")) {
// 			// // Dia 20
// 			// // ISS_MMYYYY_2255.pdf
// 			// // System.out.println("Processando arquivo: " + file.getPath());
// 			// HashMap<String, String> data = newData(clienteId, null,"23", "ISS",
// 			// "ISS ref. " + formatMonthRef(dataBase) + "/" +
// 			// formatYearRef(dataBase), dataBase, formatDayVenc(dataBase, 20,
// 			// 1), "0");
// 			// createObrigacao(data, "ISS_MENSAL_" + formatMonthRef(dataBase) +
// 			// "_" + formatYearRef(dataBase) + ".pdf", file);
// 			// return;
// 			// }

// 			if (filename.startsWith("LIVRODIARIO_MENSAL_") || filename.startsWith("LIVRO_DIARIO_MENSAL_")) {
// 				// Dia 15
// 				// LIVRODIARIO_MENSAL_MM_YYYY.PDF
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "24", "Livro Di�rio",
// 						"Livro Di�rio ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
// 						formatDayVenc(dataBase, 15, 0), "0", "E");
// 				createObrigacao(data,
// 						"LIVRO_DIARIO_MENSAL_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf",
// 						file,request);
// 				return;
// 			}

// 			if (filename.startsWith("RECIBO DE ADIANTAMENTO") || filename.startsWith("RECIBO_ADIANTAMENTO_MENSAL_")) {
// 				// Recibo de Adiantamento YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(
// 						clienteId, null, "34", "Recibo Adiant. Func.", "Recibo de Adiantamento - Funcion�rios ref. "
// 								+ formatMonthRef(dataBase) + "/" + formatYearRef(dataBase),
// 						dataBase, formatDayVenc(dataBase, 5, 0), "0", "E");
// 				createObrigacao(data, "RECIBO_ADIANTAMENTO_MENSAL_" + formatMonthRef(dataBase) + "_"
// 						+ formatYearRef(dataBase) + ".pdf", file,request);
// 				return;
// 			}
// 			if (filename.startsWith("RECIBO DE PAGAMENTO") || filename.startsWith("RECIBO_PAGAMENTO_MENSAL_")) {
// 				// Recibo de Pagamento YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(
// 						clienteId, null, "29", "Recibo Pgto. Func.", "Recibo de Pagamento - Funcion�rios ref. "
// 								+ formatMonthRef(dataBase) + "/" + formatYearRef(dataBase),
// 						dataBase, formatDayVenc(dataBase, 5, 1), "0", "E");
// 				createObrigacao(data,
// 						"RECIBO_PAGAMENTO_MENSAL_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf",
// 						file,request);
// 				return;
// 			}

// 			if (filename.startsWith("RECIBO DE PARTICIPACAO NOS LUCROS")
// 					|| filename.startsWith("RECIBO_PARTICIPACAO_LUCROS_")) {
// 				// Recibo de Participa��o nos Lucros YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(
// 						clienteId, null, "47", "Recibo Participa��o Lucros", "Recibo de Participa��o de Lucros ref. "
// 								+ formatMonthRef(dataBase) + "/" + formatYearRef(dataBase),
// 						dataBase, formatDayVenc(dataBase, 5, 1), "0", "E");
// 				createObrigacao(data, "RECIBO_PARTICIPACAO_LUCROS_" + formatMonthRef(dataBase) + "_"
// 						+ formatYearRef(dataBase) + ".pdf", file,request);
// 				return;
// 			}

// 			if (filename.startsWith("RECIBO DE PRO LABORE") || filename.startsWith("RECIBO_PROLABORE_MENSAL_")) {
// 				// Recibo de Pro Labore YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "28", "Recibo Pgto. S�cios",
// 						"Recibo de Pagamento - S�cios ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase),
// 						dataBase, formatDayVenc(dataBase, 5, 1), "0", "E");
// 				createObrigacao(data,
// 						"RECIBO_PROLABORE_MENSAL_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf",
// 						file,request);
// 				return;
// 			}

// 			if (filename.startsWith("RECIBO DE 13 SALARIO 1 PARCELA")
// 					|| filename.startsWith("RECIBO_13o_SALARIO_1PARCELA_")) {
// 				// Recibo de 13 Salario 1 Parcela YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "45", "Recibo 13� sal�rio - 1� Parcela",
// 						"Recibo de 13� sal�rio - 1� Parcela", dataBase, formatDayVenc(dataBase, 20, 0), "0", "E");
// 				createObrigacao(data, "RECIBO_13o_SALARIO_1PARCELA_" + formatYearRef(dataBase) + ".pdf", file,request);
// 				return;
// 			}
// 			if (filename.startsWith("RECIBO DE 13 SALARIO 2 PARCELA")
// 					|| filename.startsWith("RECIBO_13o_SALARIO_2PARCELA_")) {
// 				// Recibo de 13 Salario 2 Parcela YYYYMM.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "46", "Recibo 13� sal�rio - 2� Parcela",
// 						"Recibo de 13� sal�rio - 2� Parcela", dataBase, formatDayVenc(dataBase, 20, 0), "0", "E");
// 				createObrigacao(data, "RECIBO_13o_SALARIO_2PARCELA_" + formatYearRef(dataBase) + ".pdf", file,request);
// 				return;
// 			}
// //Alteracao: 04/012/2018 - Solicitacao Stephanie
// // - nao subri automaticamente o Relatorio Razao - irao subir manualmente
// //			if (filename.startsWith("RELATORIORAZAO_")) {
// //				// Dia 15
// //				// RELATORIORAZAO_DDMM_DDMM_YYYY_999999999_999999999.PDF
// //				// System.out.println("Processando arquivo: " + file.getPath());
// //				HashMap<String, String> data = newData(clienteId, null,"24", "Livro Raz�o", "Livro Raz�o ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
// //						formatDayVenc(dataBase, 15, 0), "0");
// //				createObrigacao(data, "RELATORIO_RAZAO_MENSAL_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf", file);
// //				return;
// //			}

// 			if (filename.startsWith("RPS")) {
// 				// RPS YYYYMM - Aut�nomos.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "48", "Recibo e Folha de Pagamento - Aut�nomos",
// 						"Recibo e Folha de Pagamento - Aut�nomos ref. " + formatMonthRef(dataBase) + "/"
// 								+ formatYearRef(dataBase),
// 						dataBase, formatDayVenc(dataBase, 5, 1), "0", "E");
// 				createObrigacao(data, "RPS_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf", file,request);
// 				return;
// 			}

// 			if (filename.startsWith("SEFIP") || filename.startsWith("PROTOCOLO_SEFIP_MENSAL_")) {
// 				// Dia 6
// 				// SEFIP.PDF
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "35", "Protocolo SEFIP",
// 						"SEFIP ref. " + formatMonthRef(dataBase) + "/" + formatYearRef(dataBase), dataBase,
// 						formatDayVenc(dataBase, 6, 1), "0", "E");
// 				createObrigacao(data,
// 						"PROTOCOLO_SEFIP_MENSAL_" + formatMonthRef(dataBase) + "_" + formatYearRef(dataBase) + ".pdf",
// 						file,request);
// 				return;
// 			}

// 			if (filename.startsWith("SOLICITACAOVALETRANSPORTE")
// 					|| filename.startsWith("SOLICITACAO_VALE_TRANSPORTE")) {
// 				// SolicitacaoValeTransporte.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "55", "Solicita��o de Vale-Transporte",
// 						"Solicita��o de Vale-Transporte", dataBase, formatDayVenc(dataBase, 1, 0), "0", "E");
// 				createObrigacao(data, "SOLICITACAO_VALE_TRANSPORTE.pdf", file,request);
// 				return;
// 			}

// 			if (filename.startsWith("TERMORESCISAO") || filename.startsWith("TERMO_RECISAO")) {
// 				// TermoRescisao.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "56", "Termo de Rescis�o", "Termo de Rescis�o",
// 						dataBase, formatDayVenc(dataBase, 28, 0), "0", "E");
// 				createObrigacao(data, "TERMO_RECISAO.pdf", file,request);
// 				return;
// 			}
// 			if (filename.startsWith("TERMORESPOSABILIDADE") || filename.startsWith("TERMO_RESPONSABILIDADE")) {
// 				// TermoResposabilidade.pdf
// 				// System.out.println("Processando arquivo: " + file.getPath());
// 				HashMap<String, String> data = newData(clienteId, null, "57", "Termo de Responsabilidade",
// 						"Termo de Responsabilidade", dataBase, formatDayVenc(dataBase, 28, 0), "0", "E");
// 				createObrigacao(data, "TERMO_RESPONSABILIDADE.pdf", file,request);
// 				return;
// 			}

// 			// if (filename.startsWith("BALANCETEDEVERIFICACAO_")) {
			// if (filename.indexOf("_ANUAL_") > -1) {
			// // BALANCETEDEVERIFICACAO_ANUAL_DD_MM_YYYY.PDF
			// // System.out.println("Processando arquivo: " + file.getPath());
			// HashMap<String, String> data = newData(clienteId, null,"",
			// "Balancete de Verifica��o", "Balancete de Verifica��o ref. ",
			// dataEmissao, null, null);
			// createObrigacao(data, "BALANCETE_VERIFICACAO_ANUAL_.pdf", file);
			// return;
			// }
			//
			// if (filename.indexOf("_MENSAL_") > -1) {
			// // BALANCETEDEVERIFICACAO_MENSAL_MM_YYYY.PDF
			// // System.out.println("Processando arquivo: " + file.getPath());
			// HashMap<String, String> data = newData(clienteId, null,"",
			// "Balancete de Verifica��o", "Balancete de Verifica��o ref. ",
			// dataEmissao, null, null);
			// createObrigacao(data, "BALANCETE_VERIFICACAO_MENSAL_.pdf", file);
			// return;
			// }
			// }
			// if (filename.startsWith("BALANCOABERTURA_ANUAL_")) {
			// // BALANCOABERTURA_ANUAL_YYYY.PDF
			// // System.out.println("Processando arquivo: " + file.getPath());
			// HashMap<String, String> data = newData(clienteId, null,"",
			// "Balanco de Abertura", "Balanco de Abertura ref. ", dataEmissao,
			// null, null);
			// createObrigacao(data, "BALANCO_ABERTURA_ANUAL_.pdf", file);
			// return;
			// }

			// if (filename.startsWith("GRFC")) {
			// // GRFC.pdf
			// // System.out.println("Processando arquivo: " + file.getPath());
			// return;
			// }
			// if (filename.startsWith("LIVRO_ENTRADA_")) {
			// // LIVRO_ENTRADA_MMYYYY.PDF
			// // System.out.println("Processando arquivo: " + file.getPath());
			// return;
			// }
			// if (filename.startsWith("LIVRO_SAIDA_")) {
			// // LIVRO_SAIDA_MMYYYY.PDF
			// // System.out.println("Processando arquivo: " + file.getPath());
			// return;
			// }
			// if (filename.startsWith("LIVRO_SAIDA_NOVO_")) {
			// // LIVRO_SAIDA_NOVO_MMYYYY.PDF
			// // System.out.println("Processando arquivo: " + file.getPath());
			// return;
			// }
			// if (filename.startsWith("LIVRO_SAIDA_PARCIAL_")) {
			// // LIVRO_SAIDA_PARCIAL_MMYYYY.PDF
			// // System.out.println("Processando arquivo: " + file.getPath());
			// return;
			// }

			// if (filename.startsWith("RELATORIOANALISECONTABIL_")) {
			// // RELATORIOANALISECONTABIL_DD_MM_YYYY.PDF
			// // System.out.println("Processando arquivo: " + file.getPath());
			// return;
			// }
			// if (filename.startsWith("RELATORIODRE_MODELO2_TRIMESTRAL_")) {
			// // RELATORIODRE_MODELO2_TRIMESTRAL_1TRI_DD_MM_YYYY.PDF
			// // RELATORIODRE_MODELO2_TRIMESTRAL_2TRI_DD_MM_YYYY.PDF
			// // RELATORIODRE_MODELO2_TRIMESTRAL_3TRI_DD_MM_YYYY.PDF
			// // RELATORIODRE_MODELO2_TRIMESTRAL_4TRI_DD_MM_YYYY.PDF
			// // System.out.println("Processando arquivo: " + file.getPath());
			// return;
			// }
			// if (filename.startsWith("RELATORIORAZAO_")) {
			// // RELATORIORAZAO_DDMM_DDMM_YYYY_999999999_999999999.PDF
			// // System.out.println("Processando arquivo: " + file.getPath());
			// return;
			// }

			// if (filename.startsWith("SERV_TOMADO_56_")) {
			// // SERV_TOMADO_56_MMYYYY.PDF
			// // System.out.println("Processando arquivo: " + file.getPath());
			// return;
			// }

		}
	}

	private String formatDayVenc(String dataBase, int day, int competencia) {

		if (dataBase != null) {
			int year = Integer.parseInt(dataBase.substring(0, 4));
			int month = Integer.parseInt(dataBase.substring(5, 7));

			if (competencia != 0) {
				month = month + competencia;
			}

			while (month > 12) {
				month = month - 12;
				year = year + 1;
			}

			return String.format("%04d-%02d-%02d", year, month, day) + dataBase.substring(10);
		}

		return null;
	}

	private String formatMonthRef(String dataBase) {

		if (dataBase != null) {
			return dataBase.substring(5, 7);
		}

		return null;
	}

	private String formatYearRef(String dataBase) {

		if (dataBase != null) {
			return dataBase.substring(0, 4);
		}

		return null;
	}

	private String formatLastYearDayRef(String dataBase) {

		if (dataBase != null) {
			int year = Integer.parseInt(dataBase.substring(0, 4).trim());
			year = year - 1;
			return String.format("%04d", year) + "-12-31T12:00:00.000Z";
		}

		return null;
	}

	

	private String parseDatePath(String path, ModelRequest request) {



		String data = request.getData();

		

		System.out.println(data);
		if (data == null || data.length() != 6) {
			return null;
		}
	
		try {
			int mes = Integer.parseInt(data.substring(0, 2));
			int ano = Integer.parseInt(data.substring(2));
			
			// Cria um objeto Calendar com a data 01/mes/ano
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, mes - 1);
			cal.set(Calendar.YEAR, ano);
	
			// Obtém a data formatada
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			return sdf.format(cal.getTime());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	
		return null;
	
	
	}

	

	private void createFile(File bckFile, File newFile) throws IOException {
		if (newFile.exists()) {
			newFile.delete();
		}

		copyFile(bckFile, newFile);
	}

	private File backupFile(File file) throws IOException {

		String filename = file.getName() + ".bkp";

		// File (or directory) with new name
		File file2 = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator))
				+ File.separator + filename);

		if (file2.exists()) {
			file2.delete();
		}

		// Rename file (or directory)
		renameFile(file, filename);

		return file2;
	}

	private void undoBackupFile(File file) {

		try {

			String filename = file.getAbsolutePath();

			if (filename.endsWith(".bkp")) {
				filename = filename.substring(0, filename.length() - 4);
			}

			// Rename file (or directory)
			renameFile(file, filename);

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void copyFile(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;

		try {

			is = new FileInputStream(source);
			os = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];
			int length;

			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}

		} finally {

			if (is != null) {
				is.close();
			}

			if (os != null) {
				os.close();
			}

		}
	}

	private File renameInvalidFile(File file) {

		try {

			String name = file.getName();

			if (name.lastIndexOf(".") > 0) {
				String ext = name.substring(name.lastIndexOf(".") + 1);
				name = name.substring(0, name.lastIndexOf(".")) + ".INVALIDO." + ext.toLowerCase();
			}

			return this.renameFile(file, name);

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return file;
	}

	private File renameFile(File file, String name) throws IOException {

		// File (or directory) with new name
		File file2 = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator))
				+ File.separator + name);

		if (file2.exists()) {
			file2.delete();
		}

		// Rename file (or directory)
		boolean success = file.renameTo(file2);

		if (success) {
			// System.out.println("File [" + file.getAbsolutePath() + "] renamed to [" +
			// file2.getAbsolutePath() + "].");
		}

		return file2;
	}

	private void saveErrorFile(File file, Throwable t) {

		try {

			if (t != null) {
				File file2 = new File(file.getAbsolutePath() + ".err");

				PrintWriter pw = new PrintWriter(file2);
				t.printStackTrace(pw);
				pw.close();
			}

		} catch (Throwable t2) {
			t.printStackTrace();
		}
	}



}

package com.sync.sync.sync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import java.util.Iterator;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;


public class PdfParser {

	public HashMap<String, String> parseDARF(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processDARF(content);
	}

	private HashMap<String, String> processDARF(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			String lastLine = null;
			String lastLine2 = null;
			String lastLine3 = null;
			String lastLine4 = null;
			String lastLine5 = null;
			int tipo = 0;
			while ((line = reader.readLine()) != null) {

				if (tipo == 0 && lastLine != null && lastLine.startsWith("01 - Nome/Telefone")) {
					if (line.indexOf("Cálculo p/ pag até") > -1) {
						// DARF COFINS (2172)
						tipo = 2172;
					} else {
						// DARF Pagamento (0561)
						tipo = 0561;
					}
				}
				if (tipo == 0561) {

					if (lastLine != null && lastLine.startsWith("01 - Nome/Telefone")
							&& !data.containsKey("CD_OPERACAO")) {
						data.put("CD_OPERACAO", line.trim());
					}

					if (lastLine != null && lastLine.startsWith("- - - - - - - -") && !line.startsWith("DARF")
							&& !data.containsKey("DT_REF")) {
						data.put("DT_REF", line.trim());
					}

					if (line.startsWith("09 -Valor do juros") && !data.containsKey("VALOR")) {
						data.put("VALOR", convertToDouble(lastLine.trim()));
					}

					if (line.startsWith("- Fica vedado o recolhimento de valor") && !data.containsKey("CPF/CNPJ")) {
						data.put("CPF/CNPJ", (lastLine2 == null ? null : lastLine2.trim()));

						if (lastLine.trim().length() > 10) {
							int pos = lastLine.lastIndexOf(" ");
							data.put("DT_VENC", lastLine.substring(pos).trim());
						} else {
							data.put("DT_VENC", lastLine.trim());
						}
					}
				} else if (tipo == 2172) {
					if (lastLine2 != null && lastLine2.startsWith("Cálculo p/ pag até")
							&& !data.containsKey("CD_OPERACAO")) {
						data.put("CD_OPERACAO", line.trim());
					}

					if (lastLine != null && lastLine.startsWith("Cálculo p/ pag até")
							&& !data.containsKey("DT_REF")) {
						data.put("DT_REF", line.trim());
					}

					if (line.startsWith("09 -Valor do juros e/ou") && !data.containsKey("VALOR")) {
						data.put("VALOR", convertToDouble(lastLine5.trim()));
					}

					if (line.startsWith("09 -Valor do juros e/ou") && !data.containsKey("CPF/CNPJ")) {
						data.put("DT_VENC", (lastLine2 == null ? null : lastLine2.trim()));
						data.put("CPF/CNPJ", (lastLine == null ? null : lastLine.trim()));
					}
				}

				lastLine5 = lastLine4;
				lastLine4 = lastLine3;
				lastLine3 = lastLine2;
				lastLine2 = lastLine;
				lastLine = line;
			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseGPS(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processGPS(content);
	}

	private HashMap<String, String> processGPS(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			boolean fg = false;
			String lastLine = null;
			int counter = 0;
			while ((line = reader.readLine()) != null) {

				if (fg) {

					if (counter > 0) {
						counter++;
						if (counter == 3) {
							data.put("CD_OPERACAO", line.trim());
							// // System.out.println("****>>>> Código Receita: " +
							// line.trim());
						}
						if (counter == 4) {
							data.put("DT_REF", line.trim());
							// // System.out.println("****>>>> Data Referência: " +
							// line.trim());
						}
						if (counter == 5) {
							data.put("CPF/CNPJ", line.trim());
							// // System.out.println("****>>>> Data Referência: " +
							// line.trim());
							counter = 0;
						}
					}

					if (lastLine != null && lastLine.startsWith("11 - TOTAL")) {
						fg = true;
						counter++;
					}

					if (line.startsWith("12 - AUTENTICAÇÃO BANCÁRIA")) {
						// // System.out.println("****>>>> Valor: " + lastLine);
						fg = false;
						data.put("VALOR", convertToDouble(lastLine.trim()));
					}

				} else {
					if (line.startsWith("11 - TOTAL")) {
						fg = true;
					}

					if (line.startsWith("VENCIMENTO: ")) {
						// // System.out.println("****>>>> Data Vencimento: " +
						// line.substring(12));
						data.put("DT_VENC", line.substring(12).trim());
					}
				}

				lastLine = line;
			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseGRF(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processGRF(content);
	}

	private HashMap<String, String> processGRF(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			boolean fg = false;
			String lastLine = null;
			while ((line = reader.readLine()) != null) {

				if (fg) {

					if (lastLine.startsWith("15-TOTAL A RECOLHER")) {
						// // System.out.println("****>>>> Valor: " + line);
						fg = false;
						data.put("VALOR", convertToDouble(line.trim()));
					}

					if (lastLine.startsWith("12-DATA DE VALIDADE")) {
						// // System.out.println("****>>>> Data Vencimento: " +
						// line);
						fg = false;
						data.put("DT_VENC", line.trim());
					}

					if (lastLine.startsWith("11-COMPETÊNCIA")) {
						// // System.out.println("****>>>> Data Referência: " +
						// line);
						fg = false;
						data.put("DT_REF", line.trim());
					}

					if (line.startsWith("04-SIMPLES")) {
						// // System.out.println("****>>>> Código Operação: " +
						// lastLine);
						fg = false;
						data.put("CD_OPERACAO", lastLine.trim());
					}

					if (lastLine.startsWith("10-INSCRIÇÃO/TIPO")) {
						// // System.out.println("****>>>> CPF/CNPJ: " +
						// line);
						fg = false;
						data.put("CPF/CNPJ", line.trim());
					}

				} else {
					if (line.startsWith("15-TOTAL A RECOLHER") || line.startsWith("12-DATA DE VALIDADE")
							|| line.startsWith("11-COMPETÊNCIA") || line.startsWith("AUTENTICAÇÃO MECÂNICA")
							|| line.startsWith("10-INSCRIÇÃO/TIPO")) {
						fg = true;
					}
				}

				lastLine = line;

			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseRAGPS(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processRAGPS(content);
	}

	private HashMap<String, String> processRAGPS(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			String lastLine = null;
			String lastLine2 = null;
			boolean fg = false;
			int counter = 0;
			while ((line = reader.readLine()) != null) {

				if (fg) {

					counter++;

					if (counter == 1 && !data.containsKey("VALOR")) {
						int pos = line.indexOf(" ");
						data.put("VALOR", convertToDouble(line.substring(0, pos).trim()));
					}

					if (counter == 2 && !data.containsKey("CD_OPERACAO")) {
						int pos = line.lastIndexOf(" ");
						data.put("CD_OPERACAO", line.substring(pos).trim());
					}

					if (counter == 4 && !data.containsKey("CPF/CNPJ")) {
						data.put("CPF/CNPJ", line.trim());
					}
				}

				if (line.startsWith("-------------------------------------")) {
					if (data.containsKey("DT_REF")) {
						fg = true;
					}
				}

				if (lastLine != null && lastLine.startsWith("HORA:") && !data.containsKey("DT_REF")) {
					data.put("DT_REF", line.trim());
				}

				if (lastLine2 != null && lastLine2.startsWith("HORA:") && !data.containsKey("DT_VENC")) {
					data.put("DT_VENC", line.trim());
				}

				lastLine2 = lastLine;
				lastLine = line;
			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseRAGRF(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processRAGRF(content);
	}

	private HashMap<String, String> processRAGRF(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			String lastLine = null;
			String lastLine2 = null;
			while ((line = reader.readLine()) != null) {

				if (lastLine != null && lastLine.startsWith("COMPETÊNCIA:") && !data.containsKey("CPF/CNPJ")) {
					data.put("CPF/CNPJ", line.trim());
				}

				if (line.startsWith("VALIDADE DO CÁLCULO:") && !data.containsKey("VALOR")) {
					data.put("VALOR", convertToDouble(lastLine2));
				}

				if (line.startsWith("COMPETÊNCIA:") && !data.containsKey("DT_REF")) {
					data.put("DT_REF", line.trim().substring(13, 21));

					if (line.indexOf("SIMPLES:") > -1 && !data.containsKey("CD_OPERACAO")) {
						int pos1 = line.indexOf("SIMPLES:") + 8;
						int pos2 = line.indexOf(" ", pos1);
						data.put("CD_OPERACAO", line.substring(pos1, pos2));
					}
				}

				if (lastLine != null && lastLine.startsWith("HORA:") && !data.containsKey("DT_VENC")) {
					data.put("DT_VENC", line.trim());
				}

				lastLine2 = lastLine;
				lastLine = line;
			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseRTSEFIP(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processRTSEFIP(content);
	}

	private HashMap<String, String> processRTSEFIP(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			String lastLine = null;
			while ((line = reader.readLine()) != null) {

				if (lastLine != null && lastLine.startsWith("INSCRIÇÃO:") && !data.containsKey("CPF/CNPJ")) {
					int pos = line.lastIndexOf(" ");
					data.put("CPF/CNPJ", line.substring(pos).trim());
				}

				if (lastLine != null && lastLine.startsWith("TOMADOR/OBRA:") && !data.containsKey("VALOR")) {
					String[] tokens = line.split(" ");
					if (tokens.length == 2) {
						data.put("VALOR", convertToDouble(tokens[0].trim()));
					}
				}

				if (line.startsWith("FPAS:COMP:") && !data.containsKey("DT_REF")) {
					int pos1 = 10;
					int pos2 = line.indexOf(" ", 11);
					data.put("DT_REF", line.substring(pos1, pos2).trim());

					if (line.indexOf("COD REC:") > -1 && !data.containsKey("CD_OPERACAO")) {
						int pos21 = line.indexOf("COD REC:") + 8;
						int pos22 = line.indexOf(" ", pos21);
						data.put("CD_OPERACAO", line.substring(pos21, pos22));
					}

				}

				if (lastLine != null && lastLine.startsWith("PÁG :") && !data.containsKey("DT_VENC")) {
					data.put("DT_VENC", line.trim());
				}

				lastLine = line;
			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseREFerias(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processREFerias(content);
	}

	private HashMap<String, String> processREFerias(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			while ((line = reader.readLine()) != null) {

				if (line.indexOf("Apelido") > -1 && !data.containsKey("CPF/CNPJ")) {
					data.put("CPF/CNPJ", line.substring(0, line.indexOf("Apelido")).trim());
				}
				if (line.indexOf("Relação de Escala de Férias") > -1 && !data.containsKey("DT_VENC")) {
					int pos = line.indexOf("Relação de Escala de Férias");
					data.put("DT_VENC", line.substring(pos + 28, pos + 28 + 11).trim());
				}
				if (line.indexOf("REFERÊNCIA:") > -1) {
					data.put("DT_REF", line.substring(line.indexOf("REFERÊNCIA:") + 11).trim());
				}
			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseFPProLabore(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processFPProLabore(content);
	}

	private HashMap<String, String> processFPProLabore(String content) {

		HashMap<String, String> data = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			boolean fg = false;
			int counter = 0;
			while ((line = reader.readLine()) != null) {

				if (line.indexOf("CNPJ/CEI:") > -1 && line.indexOf("Inscrição:") > -1
						&& !data.containsKey("CPF/CNPJ")) {
					data.put("CPF/CNPJ",
							line.substring(line.indexOf("CNPJ/CEI:") + 9, line.indexOf("Inscrição:")).trim());
				}

				if (line.indexOf("Período de:") > -1 && !data.containsKey("DT_REF")) {
					int pos = line.lastIndexOf("a");
					data.put("DT_REF", line.substring(pos + 1, pos + 12).trim());
					data.put("DT_VENC", line.substring(pos + 1, pos + 12).trim());
				}

				if (fg) {
					counter++;
					if (counter == 3 && !data.containsKey("VALOR")) {
						String[] tokens = line.split(" ");
						data.put("VALOR", convertToDouble(tokens[3].trim()));
					}
				}

				if (line.indexOf("R E S U M O   G E R A L") > -1) {
					fg = true;
					counter = 0;
				}

			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseFPFunc(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processFPFunc(content);
	}

	private HashMap<String, String> processFPFunc(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			boolean fg = false;
			int counter = 0;
			while ((line = reader.readLine()) != null) {

				if (line.indexOf("CNPJ/CEI:") > -1 && line.indexOf("Inscrição:") > -1
						&& !data.containsKey("CPF/CNPJ")) {
					data.put("CPF/CNPJ",
							line.substring(line.indexOf("CNPJ/CEI:") + 9, line.indexOf("Inscrição:")).trim());
				}

				if (line.indexOf("Período de:") > -1 && !data.containsKey("DT_REF")) {
					int pos = line.lastIndexOf("a");
					data.put("DT_REF", line.substring(pos + 1, pos + 12).trim());
					data.put("DT_VENC", line.substring(pos + 1, pos + 12).trim());
				}

				if (fg) {
					counter++;
					if (counter == 3 && !data.containsKey("VALOR")) {
						String[] tokens = line.split(" ");
						data.put("VALOR", convertToDouble(tokens[3].trim()));
					}
				}

				if (line.indexOf("R E S U M O   G E R A L") > -1) {
					fg = true;
					counter = 0;
				}

			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseRPFunc(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processRPFunc(content);
	}

	private HashMap<String, String> processRPFunc(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			String lastLine = null;
			double value = 0.0;
			while ((line = reader.readLine()) != null) {

				if (lastLine != null && lastLine.indexOf("Código Nome Cbo Empresa Local Depto Setor Secao Folha") > -1
						&& !data.containsKey("CPF/CNPJ")) {
					data.put("CPF/CNPJ", line.substring(0, 18).trim());
				}

				if (line.indexOf("RECIBO DE PAGAMENTO") > -1 && !data.containsKey("DT_REF")) {
					int pos1 = lastLine.lastIndexOf(" ");
					int pos2 = lastLine.lastIndexOf("MENSAL");
					String date = formatDate(lastLine.substring(pos1 + 1, pos2).trim());
					data.put("DT_REF", date);
					data.put("DT_VENC", date);
				}

				if (line.indexOf("Total Liquido -->") > -1 && !data.containsKey("VALOR")) {
					int pos = line.trim().lastIndexOf(" ");
					String v = line.trim().substring(pos);
					v = v.replaceAll("\\.", "");
					v = v.replaceAll(",", ".");
					value += Double.valueOf(v);
				}

				lastLine = line;
			}

			data.put("VALOR", String.valueOf(value));

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	private String formatDate(String date) {

		if (date != null) {
			date = date.toUpperCase();

			date = date.replaceAll("JANEIRO", "01").replaceAll("FEVEREIRO", "02").replaceAll("MARÇO", "03")
					.replaceAll("ABRIL", "04");
			date = date.replaceAll("MAIO", "05").replaceAll("JUNHO", "06").replaceAll("JULHO", "07")
					.replaceAll("AGOSTO", "08");
			date = date.replaceAll("SETEMBRO", "09").replaceAll("OUTUBRO", "10").replaceAll("NOVEMBRO", "11")
					.replaceAll("DEZEMBRO", "12");
		}

		return date;
	}

	private String formatAbrevDate(String date) {

		if (date != null) {
			date = date.toUpperCase();

			date = date.replaceAll("JAN", "01").replaceAll("FEV", "02").replaceAll("MAR", "03").replaceAll("ABR", "04");
			date = date.replaceAll("MAI", "05").replaceAll("JUN", "06").replaceAll("JUL", "07").replaceAll("AGO", "08");
			date = date.replaceAll("SET", "09").replaceAll("OUT", "10").replaceAll("NOV", "11").replaceAll("DEZ", "12");
		}

		return date;
	}

	public HashMap<String, String> parseProtocoloSEFIP(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processProtocoloSEFIP(content);
	}

	private HashMap<String, String> processProtocoloSEFIP(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			while ((line = reader.readLine()) != null) {

				line = line.trim();

				if (line.startsWith("Código de Recolhimento:") && !data.containsKey("CD_OPERACAO")) {
					int pos = line.lastIndexOf(" ");
					data.put("CD_OPERACAO", line.substring(pos).trim());
				}

				if (line.startsWith("Competência:") && !data.containsKey("DT_REF")) {
					int pos = line.lastIndexOf(" ");
					data.put("DT_REF", line.substring(pos).trim());
				}

				if (line.startsWith("Inscrição Responsável:") && !data.containsKey("CPF/CNPJ")) {
					int pos = line.lastIndexOf(" ");
					data.put("CPF/CNPJ", line.substring(pos).trim());
				}

				if (line.startsWith("O número do Protocolo de Envio deste arquivo é:")
						&& !data.containsKey("NO_PROTOCOLO")) {
					int pos = line.lastIndexOf(" ");
					data.put("NO_PROTOCOLO", line.substring(pos, line.length() - 2).trim());
				}

				if (line.indexOf("foi armazenado na Caixa Econômica Federal em") > -1
						&& !data.containsKey("DT_VENC")) {
					String[] tokens = line.split(" ");
					data.put("DT_VENC", tokens[10].trim());
					data.put("NOME_ARQUIVO_SEFIP", tokens[2].trim());
				}

			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseComprovanteINSS(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processComprovanteINSS(content);
	}

	private HashMap<String, String> processComprovanteINSS(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			String lastLine = null;
			String lastLine2 = null;
			boolean fg = false;
			while ((line = reader.readLine()) != null) {

				line = line.trim();

				if (fg) {
					String[] tokens = line.split(" ");

					if (!data.containsKey("CD_OPERACAO")) {
						data.put("CD_OPERACAO", tokens[2].trim());
					}

					if (!data.containsKey("DT_REF")) {
						data.put("DT_REF", tokens[0].trim());
					}

					fg = false;
				}

				if (lastLine != null && lastLine.startsWith("Nº ARQUIVO:") && !data.containsKey("CPF/CNPJ")) {
					int pos = line.lastIndexOf(" ");
					data.put("CPF/CNPJ", line.substring(pos).trim());
				}

				if (line.startsWith("ALIQ RAT:") && !data.containsKey("NO_PROTOCOLO")) {
					data.put("NO_PROTOCOLO", lastLine.trim());
					if (lastLine2 != null) {
						data.put("NOME_ARQUIVO_INSS", lastLine2.trim());
					}
				}

				if (line.startsWith("GFIP - SEFIP") && !data.containsKey("DT_VENC")) {
					data.put("DT_VENC", lastLine.trim());
				}

				if (line.indexOf("APURAÇÃO DO VALOR A RECOLHER:") > -1) {
					fg = true;
				}

				lastLine2 = lastLine;
				lastLine = line;
			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseContribuicaoSindical(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processContribuicaoSindical(content);
	}

	private HashMap<String, String> processContribuicaoSindical(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			String lastLine = null;
			while ((line = reader.readLine()) != null) {

				if (lastLine != null && lastLine.startsWith("  Código da Entidade Sindical")
						&& !data.containsKey("CD_OPERACAO")) {
					data.put("CD_OPERACAO", line.trim().substring(3));
				}

				if (line.indexOf("CNPJ:") > -1 && !data.containsKey("CPF/CNPJ")
						&& line.indexOf("Sacador/Avalista:") == -1) {
					data.put("CPF/CNPJ", line.trim().substring(line.indexOf("CNPJ:") + 5));
				}

				if (lastLine != null && lastLine.startsWith(" Valor do Documento") && !data.containsKey("VALOR")) {
					data.put("VALOR", convertToDouble(line.trim().substring(3)));
				}

				if (lastLine != null && lastLine.startsWith("  Exercício") && !data.containsKey("DT_REF")) {
					data.put("DT_REF", line.trim().substring(3));
				}

				if (lastLine != null && lastLine.startsWith("  Data de Vencimento") && !data.containsKey("DT_VENC")) {
					data.put("DT_VENC", line.trim().substring(3));
				}

				lastLine = line;
			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseContribuicaoAssistencial(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processContribuicaoAssistencial(content);
	}

	private HashMap<String, String> processContribuicaoAssistencial(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			String lastLine = null;
			String lastLine2 = null;
			while ((line = reader.readLine()) != null) {

				if (lastLine2 != null && lastLine2.startsWith("Pagador:") && !data.containsKey("CPF/CNPJ")) {
					int pos = line.lastIndexOf("CNPJ:");
					data.put("CPF/CNPJ", line.substring(pos + 5).trim());
				}

				if (line.startsWith("R$") && line.length() > 2 && !data.containsKey("VALOR")) {
					data.put("VALOR", convertToDouble(line.substring(2).trim()));
					if (lastLine != null && !data.containsKey("CD_OPERACAO")) {
						data.put("CD_OPERACAO", lastLine.trim().substring(1));
					}
				}

				if (line != null && line.indexOf(" REF. PARCELA") > -1 && !data.containsKey("DT_REF")) {
					int pos = line.lastIndexOf(" REF. PARCELA");
					data.put("DT_REF", line.substring(pos + 13).trim());
				}

				if (lastLine2 != null && lastLine2.startsWith("Local de pagamento") && line.length() == 10
						&& !data.containsKey("DT_VENC")) {
					data.put("DT_VENC", line.trim());
				}

				lastLine2 = lastLine;
				lastLine = line;
			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseRTLiquidos(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processRTLiquidos(content);
	}

	private HashMap<String, String> processRTLiquidos(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			String lastLine = null;
			while ((line = reader.readLine()) != null) {

				line = line.trim();

				if (line.startsWith("Bairro:") && !data.containsKey("CPF/CNPJ")) {
					data.put("CPF/CNPJ", lastLine.trim());
				}

				if (line.indexOf("Total: ") > -1 && !data.containsKey("VALOR")) {
					data.put("VALOR", convertToDouble(line.substring(7).trim()));
				}

				if (line.startsWith("Nº:") && !data.containsKey("DT_REF")) {
					String[] tokens = lastLine.split(" ");
					data.put("DT_REF", tokens[1].trim());
					data.put("DT_VENC", tokens[1].trim());
				}

				lastLine = line;
			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	private String readContent(String filename) {

		File f = new File(filename);
		FileInputStream is = null;
		try {
			is = new FileInputStream(f);
		} catch (IOException e) {
			// System.out.println("ERRO: " + e.getMessage());
			return null;
		}

		PDDocument pdfDocument = null;
		try {
			PDFParser parser = new PDFParser(is);
			parser.parse();
			pdfDocument = parser.getPDDocument();
			PDFTextStripper stripper = new PDFTextStripper();
			return stripper.getText(pdfDocument);
		} catch (IOException e) {
			return "ERRO: Não é possível abrir a stream" + e;
		} catch (Throwable e) {
			// Fazemos um catch, uma vez que precisamos de fechar o recurso
			return "ERRO: Um erro ocorreu enquanto tentava obter o conteúdo do PDF" + e;
		} finally {
			if (pdfDocument != null) {
				try {
					pdfDocument.close();
				} catch (IOException e) {
					return "ERRO: Não foi possível fechar o PDF." + e;
				}
			}
		}
	}

	

	public HashMap<String, String> parseGPSRFB(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processGPSRFB(content);
	}

	private HashMap<String, String> processGPSRFB(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			String lastLine = null;
			String lastLine2 = null;
			String lastLine3 = null;
			while ((line = reader.readLine()) != null) {

				if (lastLine != null && lastLine.indexOf("DATA:") > -1 && !data.containsKey("DT_REF")) {
					data.put("DT_REF", line.trim());
					int pos = lastLine.indexOf("DATA:");
					data.put("DT_VENC", lastLine.substring(pos + 6, pos + 16));
				}

				if (line.startsWith("PARA RECOLHIMENTO NO PRAZO")) {

					if (lastLine != null && !data.containsKey("VALOR")) {
						data.put("VALOR", convertToDouble(lastLine.trim()));
					}

					if (lastLine2 != null && !data.containsKey("CPF/CNPJ")) {
						data.put("CPF/CNPJ", lastLine2.trim());
					}

					if (lastLine3 != null && !data.containsKey("CD_OPERACAO")) {
						data.put("CD_OPERACAO", lastLine3.trim());
					}

				}

				lastLine3 = lastLine2;
				lastLine2 = lastLine;
				lastLine = line;
			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseFichaFinanceira(String filename) {

		String content = readContent(filename);
		//// System.out.println(content);

		return this.processFichaFinanceira(content);
	}

	private HashMap<String, String> processFichaFinanceira(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			while ((line = reader.readLine()) != null) {

				line = line.trim();

				if (line.indexOf("FICHA") > -1 && line.indexOf("FINANCEIRA") > -1 && line.indexOf("PERÍODO") > -1
						&& !data.containsKey("DT_REF")) {
					//// System.out.println("\t" + line);
					int pos = line.lastIndexOf(" ");
					data.put("DT_REF", formatAbrevDate(line.substring(pos).trim()));
				}

				if (line.indexOf("Data:") > -1 && !data.containsKey("DT_VENC")) {
					//// System.out.println("\t" + line);
					int pos = line.lastIndexOf("Data:");
					data.put("DT_VENC", line.substring(pos + 5));
				}

				if (line.indexOf("Proventos:") > -1 && line.indexOf("Descontos:") > -1 && line.indexOf("Líquido:") > -1
						&& !data.containsKey("VALOR")) {
					// System.out.println("\t" + line);
					int pos1 = line.lastIndexOf(",");
					int pos2 = line.lastIndexOf(",", pos1 - 1);
					data.put("VALOR", convertToDouble(line.substring(pos2 + 3).trim()));
				}
			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseGARE(String filename) {

		String content = readContent(filename);
		// System.out.println(content);

		return this.processGARE(content);
	}

	private HashMap<String, String> processGARE(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			while ((line = reader.readLine()) != null) {

				// line = line .trim();
				//
				// if (line.indexOf("FICHA") > -1 && line.indexOf("FINANCEIRA") > -1 &&
				// line.indexOf("PERÍODO") > -1 && !data.containsKey("DT_REF")) {
				// //// System.out.println("\t" + line);
				// int pos = line.lastIndexOf(" ");
				// data.put("DT_REF", formatAbrevDate(line.substring(pos).trim()));
				// }
				//
				// if (line.indexOf("Data:") > -1 && !data.containsKey("DT_VENC")) {
				// //// System.out.println("\t" + line);
				// int pos = line.lastIndexOf("Data:");
				// data.put("DT_VENC", line.substring(pos+5));
				// }
				//
				// if (line.indexOf("Proventos:") > -1 && line.indexOf("Descontos:") > -1 &&
				// line.indexOf("Líquido:") > -1 && !data.containsKey("VALOR")) {
				// // System.out.println("\t" + line);
				// int pos1 = line.lastIndexOf(",");
				// int pos2 = line.lastIndexOf(",", pos1-1);
				// data.put("VALOR", line.substring(pos2+3).trim());
				// }
			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseDAS(File file) {

		String content = readContent(file.getAbsolutePath());
		// System.out.println(content);

		return this.processDAS(content);
	}

	

	public HashMap<String, String> parseDAS(String filename) {

		String content = readContent(filename);
		// System.out.println(content);

		return this.processDAS(content);
	}

	private HashMap<String, String> processDAS(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			int i = 1;
			while ((line = reader.readLine()) != null) {

				if (line.startsWith("Pagar até:") && !data.containsKey("DT_VENC")) {
					int pos = line.lastIndexOf("Pagar até:");
					data.put("DT_VENC", line.substring(pos + 10).trim());
				}

				if (line.startsWith("Valor:") && !data.containsKey("VALOR")) {
					int pos = line.lastIndexOf("Valor:");
					data.put("VALOR", convertToDouble(line.substring(pos + 6).trim()));
				}

//				if (i == 22) {
//					String token = line.trim();
//					token = token.replaceAll("\\.", "");
//					token = token.replaceAll("\\,", ".");
//
//					data.put("VALOR", token.trim());
//				}
				//
				// if (line.indexOf("FICHA") > -1 && line.indexOf("FINANCEIRA") > -1 &&
				// line.indexOf("PERÍODO") > -1 && !data.containsKey("DT_REF")) {
				// //// System.out.println("\t" + line);
				// int pos = line.lastIndexOf(" ");
				// data.put("DT_REF", formatAbrevDate(line.substring(pos).trim()));
				// }
				//
				// if (line.indexOf("Data:") > -1 && !data.containsKey("DT_VENC")) {
				// //// System.out.println("\t" + line);
				// int pos = line.lastIndexOf("Data:");
				// data.put("DT_VENC", line.substring(pos+5));
				// }
				//
				// if (line.indexOf("Proventos:") > -1 && line.indexOf("Descontos:") > -1 &&
				// line.indexOf("Líquido:") > -1 && !data.containsKey("VALOR")) {
				// // System.out.println("\t" + line);
				// int pos1 = line.lastIndexOf(",");
				// int pos2 = line.lastIndexOf(",", pos1-1);
				// data.put("VALOR", line.substring(pos2+3).trim());
				// }
				i++;
			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	public HashMap<String, String> parseApuracaoICMS(String filename) {

		String content = readContent(filename);
		// // System.out.println(content);

		return this.processApuracaoICMS(content);
	}

	private HashMap<String, String> processApuracaoICMS(String content) {

		HashMap<String, String> data = new HashMap<String, String>();

		BufferedReader reader = new BufferedReader(new StringReader(content));

		try {

			String line = null;
			while ((line = reader.readLine()) != null) {

				// line = line .trim();
				//
				// if (line.indexOf("FICHA") > -1 && line.indexOf("FINANCEIRA") > -1 &&
				// line.indexOf("PERÍODO") > -1 && !data.containsKey("DT_REF")) {
				// //// System.out.println("\t" + line);
				// int pos = line.lastIndexOf(" ");
				// data.put("DT_REF", formatAbrevDate(line.substring(pos).trim()));
				// }
				//
				// if (line.indexOf("Data:") > -1 && !data.containsKey("DT_VENC")) {
				// //// System.out.println("\t" + line);
				// int pos = line.lastIndexOf("Data:");
				// data.put("DT_VENC", line.substring(pos+5));
				// }
				//
				// if (line.indexOf("Proventos:") > -1 && line.indexOf("Descontos:") > -1 &&
				// line.indexOf("Líquido:") > -1 && !data.containsKey("VALOR")) {
				// // System.out.println("\t" + line);
				// int pos1 = line.lastIndexOf(",");
				// int pos2 = line.lastIndexOf(",", pos1-1);
				// data.put("VALOR", line.substring(pos2+3).trim());
				// }
			}

			reader.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return data;
	}

	private String convertToDouble(String value) {

		if (value != null) {
			value = value.trim();
			value = value.replaceAll("\\.", "").replaceAll(",", ".");
		}

		return value;
	}

	public static void main1(String[] args) {

		HashMap<String, String> data = new HashMap<String, String>();

		PdfParser parser = new PdfParser();

		// data =
		// parser.parseDarf("C:\\tmp\\MERP\\phoenix\\contador\\1012\\2018\\01\\Folha\\DARF\\Darf
		// Pagto 0561 Periodo 20171031.pdf");
		// data =
		// parser.parseGps("C:\\tmp\\MERP\\phoenix\\contador\\1012\\2018\\01\\Folha\\GPS\\GPS
		// 2003 Pagto 201710.pdf");
		// data =
		// parser.parseGps("C:\\tmp\\MERP\\phoenix\\contador\\1012\\2018\\01\\Folha\\GPS\\GPS
		// 2950 Pagto 201711.pdf");
		// data =
		// parser.parseGRF("C:\\tmp\\MERP\\phoenix\\contador\\1012\\2018\\01\\Folha\\FGTS\\fgts
		// 112017 folha complementar vencto 07122017.pdf");
		// data =
		// parser.parseFA("C:\\tmp\\MERP\\phoenix\\contador\\1012\\2018\\01\\Folha\\RELATORIOFOLHA\\Folha
		// de Adiantamento 201710.pdf");
		// data =
		// parser.parseFA("C:\\tmp\\MERP\\phoenix\\contador\\1012\\2018\\01\\Folha\\RELATORIOFOLHA\\Folha
		// de Pagamento 201710.pdf");
		// data =
		// parser.parseFPL("C:\\tmp\\MERP\\phoenix\\contador\\1012\\2018\\01\\Folha\\RELATORIOFOLHA\\Folha
		// de Pro Labore 201710.pdf");

		// data =
		// parser.parseRTLiquidos("C:\\tmp\\MERP\\phoenix\\contador\\1012\\2018\\01\\Folha\\OBRIGAÇÕES\\Relação
		// de Totais Líquidos.pdf");

		// data =
		// parser.parseContribuicaoSindical("C:\\tmp\\MERP\\phoenix\\contador\\2132\\2018\\01\\Folha\\OBRIGAÇÕES\\Contribuição_Assistencial-27557874000109-Ref_null.LOADED.pdf");

		// data =
		// parser.parseGPSRFB("C:\\tmp\\MERP\\phoenix\\contador\\2132\\2018\\01\\Folha\\OBRIGAÇÕES\\--------GPSRFB.pdf");

		// data =
		// parser.parseRPFunc("C:\\tmp\\MERP\\phoenix\\contador\\2132\\2018\\01\\Folha\\OBRIGAÇÕES\\--------Recibo_Pagamento_Funcionários-27557874000109-Ref_201712.pdf");

		// data =
		// parser.parseDARF("C:\\tmp\\MERP\\phoenix\\contador\\2132\\2018\\01\\Folha\\OBRIGAÇÕES\\DARF(0561)-27557874000109-Ref_201801.LOADED.pdf");
		//
		// // System.out.println("\n\n\n\n");
		// if (data != null) {
		// Iterator<String> iterator = data.keySet().iterator();
		//
		// while (iterator.hasNext()) {
		// String key = iterator.next();
		// String value = data.get(key);
		// // System.out.println("\t" + key + ": " + value);
		//
		// }
		// }
		// // System.out.println("\n\n\n\n");

		// File folder = new File("C:\\tmp\\MERP\\phoenix\\modelos\\DARF\\");
		// File folder = new File("C:\\tmp\\MERP\\phoenix\\modelos\\GPS\\");
		// File folder = new File("C:\\tmp\\MERP\\phoenix\\modelos\\GRF\\");
		// File folder = new File("C:\\tmp\\MERP\\phoenix\\modelos\\SEFIP\\");
		// File folder = new File("C:\\tmp\\MERP\\phoenix\\modelos\\GARE\\");
		File folder = new File("C:\\tmp\\MERP\\phoenix\\modelos\\ICMS\\");

		File[] files = folder.listFiles();

		for (File f : files) {

			if (true || "SEFIP-0020.pdf".equalsIgnoreCase(f.getName())) {

				// System.out.println(f.getName());

				// data = parser.parseDARF(f.getAbsolutePath());
				// data = parser.parseGPS(f.getAbsolutePath());
				// data = parser.parseGRF(f.getAbsolutePath());
				// data = parser.parseFichaFinanceira(f.getAbsolutePath());
				data = parser.parseApuracaoICMS(f.getAbsolutePath());

				if (data != null) {
					Iterator<String> iterator = data.keySet().iterator();

					while (iterator.hasNext()) {
						String key = iterator.next();
						String value = data.get(key);
						// System.out.println("\t" + key + ": " + value);

					}
				}
				// System.out.println("\n\n");
			}
		}

	}

	public static void main(String[] args) {

		HashMap<String, String> data = new HashMap<String, String>();

		PdfParser parser = new PdfParser();

		File folder = new File("/home/glauco/git/mco/MCOSync/examples");

		File[] files = folder.listFiles();

		for (File f : files) {

			if (f.getName().startsWith("DAS") && f.getName().endsWith(".pdf")) {

				// System.out.println(f.getName());

				// data = parser.parseDARF(f.getAbsolutePath());
				// data = parser.parseGPS(f.getAbsolutePath());
				// data = parser.parseGRF(f.getAbsolutePath());
				// data = parser.parseFichaFinanceira(f.getAbsolutePath());
				data = parser.parseDAS(f.getAbsolutePath());

				if (data != null) {
					Iterator<String> iterator = data.keySet().iterator();

					while (iterator.hasNext()) {
						String key = iterator.next();
						String value = data.get(key);
						// System.out.println("\t" + key + ": " + value);

					}
				}
				// System.out.println("\n\n");
			}
		}

	}

}
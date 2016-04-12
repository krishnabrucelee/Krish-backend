package ck.panda.payment.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ck.panda.constants.PaymentConstants;
import ck.panda.domain.entity.PaymentGateway;
import ck.panda.service.PaymentGatewayService;

/**
 * The following code is sample code to test for the convenience of business and
 * provided businesses as needed for your site, according to technical writing ,
 * not have to use the code. The code for study and research Alipay interface ,
 * only provide a reference .
 */
@Component
public class AlipaySubmit {

	@Autowired
	private PaymentGatewayService paymentGatewayService;

	/** Aplipay gateway. */
	private static final String ALIPAY_GATEWAY_NEW = "https://www.alipay.com/cooperate/gateway.do?";

	/**
	 * Generating a signature result.
	 *
	 * @param sPara
	 *            Array to be signed.
	 * @return Signature resulting string.
	 */
	public String buildRequestMysign(Map<String, String> sPara) {
		String prestr = AlipayCore.createLinkString(sPara);
		String mysign = "";
		PaymentGateway paymentGateway = paymentGatewayService.getActivePaymentGateway(true);
		if (PaymentConstants.SIGN_TYPE.equals("MD5")) {
			mysign = ck.panda.payment.MD5.sign(prestr, paymentGateway.getSecurityCode(),
					PaymentConstants.INPUT_CHARSET);
		}
		return mysign;
	}

	/**
	 * To generate a request to the Alipay parameter array
	 *
	 * @param sParaTemp
	 *            Before the request parameter array
	 * @return To request parameter array
	 */
	private Map<String, String> buildRequestPara(Map<String, String> sParaTemp) {
		Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
		String mysign = buildRequestMysign(sPara);
		sPara.put("sign", mysign);
		sPara.put("sign_type", PaymentConstants.SIGN_TYPE);
		return sPara;
	}

	/**
	 * Establishment request to form HTML form structure ( default )
	 *
	 * @param sParaTemp
	 *            Request parameter array
	 * @param strMethod
	 *            Submission . Optional two values ​​: post, get
	 * @param strButtonName
	 *            Confirmation button display text
	 * @return Submit the form HTML text
	 */
	public String buildRequest(Map<String, String> sParaTemp, String strMethod, String strButtonName) {
		Map<String, String> sPara = buildRequestPara(sParaTemp);
		List<String> keys = new ArrayList<String>(sPara.keySet());

		StringBuffer sbHtml = new StringBuffer();

		sbHtml.append(
				"<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + ALIPAY_GATEWAY_NEW + "_input_charset="
						+ PaymentConstants.INPUT_CHARSET + " target = _blank" + "\" method=\"" + strMethod + "\">");

		for (int i = 0; i < keys.size(); i++) {
			String name = (String) keys.get(i);
			String value = (String) sPara.get(name);

			sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
		}

		sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
		sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");

		return sbHtml.toString();
	}

	/**
	 * Handler for anti-phishing , call interface query_timestamp to get
	 * timestamp Note : The remote XML parsing error , and the server is
	 * configured to support SSL and other relevant
	 *
	 * @return Timestamp string
	 * @throws IOException
	 * @throws DocumentException
	 * @throws MalformedURLException
	 */
	public String query_timestamp() throws MalformedURLException, DocumentException, IOException {

		PaymentGateway paymentGateway = paymentGatewayService.getActivePaymentGateway(true);
		String strUrl = ALIPAY_GATEWAY_NEW + "service=query_timestamp&partner=" + paymentGateway.getPartner()
				+ "&_input_charset" + PaymentConstants.INPUT_CHARSET;
		StringBuffer result = new StringBuffer();

		SAXReader reader = new SAXReader();
		Document doc = reader.read(new URL(strUrl).openStream());

		List<Node> nodeList = doc.selectNodes("//alipay/*");

		for (Node node : nodeList) {
			if (node.getName().equals("is_success") && node.getText().equals("T")) {
				List<Node> nodeList1 = doc.selectNodes("//response/timestamp/*");
				for (Node node1 : nodeList1) {
					result.append(node1.getText());
				}
			}
		}
		return result.toString();
	}
}

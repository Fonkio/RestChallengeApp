package fr.fonkio;

import org.w3c.dom.Document;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.xml.xpath.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws XPathExpressionException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://rest-challenge.herokuapp.com/");
        JsonObject token = target.path("token").request().accept(MediaType.APPLICATION_JSON).get(JsonObject.class);
        System.out.println(token);
        String tokens = token.getString("token");
        System.out.println(tokens);

        search(target, tokens, 0, Math.round(Math.pow(2, 32)));
    }

    private static void search(WebTarget target, String tokens, long min, long max) throws XPathExpressionException {
        long mid = findMidNumber(min, max);
        Document doc = target.path("try").queryParam("t", tokens).queryParam("g", mid).request().accept(MediaType.TEXT_XML_TYPE).get(Document.class);
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression xPathExpression = xPath.compile("//status/text()");
        String res = (String) xPathExpression.evaluate(doc, XPathConstants.STRING);
        System.out.println(res + " min : "+min + " mid "+ mid + " max : "+max);
        switch (res) {
            case "KO" :
                return;
            case "bravo" :
                xPath = XPathFactory.newInstance().newXPath();
                xPathExpression = xPath.compile("//message/text()");
                res = (String) xPathExpression.evaluate(doc, XPathConstants.STRING);
                System.out.println(res);
                break;
            case "trop-petit" :
                search(target, tokens, mid, max);
                break;
            case "trop-grand":

                search(target, tokens, min, mid);
                break;
        }

    }

    public static long findMidNumber(long min, long max){
        return (max-min)/2+min;
    }

}

package at.ac.tuwien.finder.datamanagement.mediation.transformer.method;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Base64.Encoder;

/**
 * This class represents a method for encoding strings with base64.
 *
 * @author Kevin Haller
 */
public class Base64EncodingMethod extends ExtensionFunctionDefinition {

    private Encoder base64Encoder = Base64.getEncoder();

    @Override
    public StructuredQName getFunctionQName() {
        return new StructuredQName("finder", "http://finder.tuwien.ac.at/function/", "base64Encode");
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[] {SequenceType.SINGLE_STRING, SequenceType.SINGLE_BOOLEAN};
    }

    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.SINGLE_STRING;
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {
            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                String response = new String(
                    base64Encoder.encode(((StringValue) arguments[0]).getStringValue().getBytes()));
                try {
                    return ((BooleanValue) arguments[1]).getBooleanValue() ?
                        StringValue.makeStringValue(URLEncoder.encode(response, "UTF-8")) :
                        StringValue.makeStringValue(response);
                } catch (UnsupportedEncodingException e) {
                    throw new XPathException(e);
                }
            }
        };
    }
}

package at.ac.tuwien.finder.datamanagement.mediation.transformer.method;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.om.ZeroOrMore;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a xsl method for extracting the post box number and street address from
 * a given full address.
 *
 * @author Kevin Haller
 */
public class PostalAddressMatcherMethod extends ExtensionFunctionDefinition {

    private static final Pattern ADDRESS_PATTERN =
        Pattern.compile("^([^\\d]*)\\s+((\\w*\\d)+([-,/,\\\\](\\w*\\d)+)*)(.*)?$");

    @Override
    public StructuredQName getFunctionQName() {
        return new StructuredQName("finder", "http://finder.tuwien.ac.at/function/",
            "addressMatcher");
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[] {SequenceType.SINGLE_STRING};
    }

    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.STRING_SEQUENCE;
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {
            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                Matcher addressMatcher =
                    ADDRESS_PATTERN.matcher(((StringValue) arguments[0]).getStringValue());
                if (addressMatcher.matches()) {
                    StringValue[] addressStringValues = new StringValue[2];
                    addressStringValues[0] = StringValue.makeStringValue(
                        addressMatcher.group(1) != null ? addressMatcher.group(1).trim() : null);
                    addressStringValues[1] = StringValue.makeStringValue(
                        addressMatcher.group(2) != null ? addressMatcher.group(2).trim() : null);
                    return new ZeroOrMore<>(addressStringValues);
                } else {
                    return new ZeroOrMore<>(new StringValue[0]);
                }
            }
        };
    }
}

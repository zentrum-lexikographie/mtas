package mtas.analysis.parser;

import mtas.analysis.token.MtasTokenCollection;
import mtas.analysis.token.MtasTokenString;
import mtas.analysis.util.MtasConfigException;
import mtas.analysis.util.MtasConfiguration;
import mtas.analysis.util.MtasParserException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;

public class MtasCSVParser extends MtasParser {
    public MtasCSVParser() {
        super();
    }

    public MtasCSVParser(MtasConfiguration config) {
        super(config);
    }

    @Override
    public MtasTokenCollection createTokenCollection(Reader reader) throws MtasParserException, MtasConfigException {
        tokenCollection = new MtasTokenCollection();
        try (CSVParser csv = new CSVParser(reader, CSVFormat.DEFAULT)) {
            for (CSVRecord record : csv) {
                final int tokenId = Integer.parseInt(record.get(0));
                final String prefix = record.get(1);
                final String postfix = record.get(2);

                final MtasTokenString token = new MtasTokenString(tokenId, prefix, postfix);

                final int parent = Integer.parseInt(record.get(3));
                if (parent >= 0) {
                    token.setProvideParentId(true);
                    token.setParentId(parent);
                }
                if ((record.size() % 2) != 0) {
                    throw new MtasParserException(
                            String.format("Odd number of CSV fields [%d]", record.getRecordNumber())
                    );
                }
                for (int fi = 4; fi < record.size(); fi += 2) {
                    token.addPositionRange(
                            Integer.parseInt(record.get(fi)),
                            Integer.parseInt(record.get(fi + 1))
                    );
                }
                token.setOffset(token.getPositionStart(), token.getPositionEnd());
                token.setRealOffset(token.getPositionStart(), token.getPositionEnd());

                tokenCollection.add(token);
            }
        } catch (IOException e) {
            throw new MtasParserException(e.getMessage());
        }

        return tokenCollection;
    }

    @Override
    public String printConfig() {
        StringBuilder text = new StringBuilder();
        text.append("=== CONFIGURATION ===\n");
        text.append(config.toString());
        text.append("=== CONFIGURATION ===\n");
        return text.toString();
    }
}

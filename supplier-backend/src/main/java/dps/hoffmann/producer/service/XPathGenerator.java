package dps.hoffmann.producer.service;

import dps.hoffmann.producer.model.instruction.ParsedInstruction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

@Service
public class XPathGenerator implements InstructionGenerator {

    public static final String RANDOMIZED_KEY_IDENTIFIER = "Randomized";


    @Value("#{${payment.xpaths}}")
    private Map<String, String> xPaths;


    @Override
    public List<String> getDisplayName() {
        List<String> out = new ArrayList<>(xPaths.keySet());
        out.add(RANDOMIZED_KEY_IDENTIFIER);
        return out;
    }

    @Override
    public Supplier<String> getSupplier(ParsedInstruction request) {
        if (request.getPathOption().equals(RANDOMIZED_KEY_IDENTIFIER)) {
            List<String> values = new ArrayList<>(xPaths.values());
            Random rand = new Random();
            return () -> values.get(rand.nextInt(values.size()));
        } else {
            String val = xPaths.get(request.getPathOption());
            return () -> val;
        }

    }




}

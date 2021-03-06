package dps.hoffmann.proxy.service;

import dps.hoffmann.proxy.model.ScalingInstruction;
import dps.hoffmann.proxy.repository.ScaleInstructionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersistenceService {

    @Autowired
    private ScaleInstructionRepository scaleInstructionRepository;

    public ScalingInstruction save(ScalingInstruction instruction) {
        return scaleInstructionRepository.save(instruction);
    }

    public List<ScalingInstruction> findAll() {
        List<ScalingInstruction> output = new ArrayList<>();
        scaleInstructionRepository.findAll().forEach(output::add);
        return output;
    }

}

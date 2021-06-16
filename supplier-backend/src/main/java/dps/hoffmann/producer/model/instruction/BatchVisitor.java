package dps.hoffmann.producer.model.instruction;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.function.Consumer;

import static java.lang.Thread.sleep;

@RequiredArgsConstructor
public class BatchVisitor {

    private final Consumer<ScalingInstruction> scaler;

    @SneakyThrows
    public void accept(WaitInstruction instruction) {
        sleep(instruction.getWaitTime());
    }

    public void accept(ScalingInstruction instruction) {
        scaler.accept(instruction);
    }
}

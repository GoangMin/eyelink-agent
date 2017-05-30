package com.m2u.eyelink.agent.profiler.instrument;


import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ASMTryCatch {
    private final MethodNode methodNode;
    private final LabelNode startLabelNode = new LabelNode();
    private final LabelNode endLabelNode = new LabelNode();

    public ASMTryCatch(final MethodNode methodNode) {
        this.methodNode = methodNode;

        final TryCatchBlockNode tryCatchBlockNode = new TryCatchBlockNode(this.startLabelNode, this.endLabelNode, this.endLabelNode, "java/lang/Throwable");
        if (this.methodNode.tryCatchBlocks == null) {
            this.methodNode.tryCatchBlocks = new ArrayList<TryCatchBlockNode>();
        }
        this.methodNode.tryCatchBlocks.add(tryCatchBlockNode);
    }

    public LabelNode getStartLabelNode() {
        return this.startLabelNode;
    }

    public LabelNode getEndLabelNode() {
        return this.endLabelNode;
    }

    public void sort() {
        if (this.methodNode.tryCatchBlocks == null) {
            return;
        }

        Collections.sort(this.methodNode.tryCatchBlocks, new Comparator<TryCatchBlockNode>() {
            @Override
            public int compare(TryCatchBlockNode o1, TryCatchBlockNode o2) {
                return blockLength(o1) - blockLength(o2);
            }

            private int blockLength(TryCatchBlockNode block) {
                final int startidx = methodNode.instructions.indexOf(block.start);
                final int endidx = methodNode.instructions.indexOf(block.end);
                return endidx - startidx;
            }
        });

        // Updates the 'target' of each try catch block annotation.
        for (int i = 0; i < this.methodNode.tryCatchBlocks.size(); ++i) {
            this.methodNode.tryCatchBlocks.get(i).updateIndex(i);
        }
    }
}
package com.m2u.eyelink.context;

public class SpanEvent extends TSpanEvent implements FrameAttachment {

    private final Span span;
    private int stackId;
    private boolean timeRecording = true;
    private Object frameObject;

    public SpanEvent(Span span) {
        if (span == null) {
            throw new NullPointerException("span must not be null");
        }
        this.span = span;
    }

    public Span getSpan() {
        return span;
    }

    public void addAnnotation(Annotation annotation) {
        this.addToAnnotations(annotation);
    }

    private void addToAnnotations(Annotation annotation) {
		// TODO Auto-generated method stub
		
	}

	public void setExceptionInfo(int exceptionClassId, String exceptionMessage) {
        final TIntStringValue exceptionInfo = new TIntStringValue(exceptionClassId);
        if (exceptionMessage != null && !exceptionMessage.isEmpty()) {
            exceptionInfo.setStringValue(exceptionMessage);
        }
        super.setExceptionInfo(exceptionInfo);
    }


    public void markStartTime() {
//        spanEvent.setStartElapsed((int) (startTime - parentSpanStartTime));
        final int startElapsed = (int)(System.currentTimeMillis() - span.getStartTime());
        
        // If startElapsed is 0, logic without mark is useless. Don't do that.
        // The first SpanEvent of a Span could result in 0. Not likely afterwards.
        this.setStartElapsed(startElapsed);
    }


	public long getStartTime() {
        return span.getStartTime() + getStartElapsed();
    }

	public void markAfterTime() {
        final int endElapsed = (int)(System.currentTimeMillis() - getStartTime());
        if (endElapsed != 0) {
            this.setEndElapsed(endElapsed);
        }
    }

	public long getAfterTime() {
        return span.getStartTime() + getStartElapsed() + getEndElapsed();
    }


	public int getStackId() {
        return stackId;
    }

    public void setStackId(int stackId) {
        this.stackId = stackId;
    }

    public boolean isTimeRecording() {
        return timeRecording;
    }

    public void setTimeRecording(boolean timeRecording) {
        this.timeRecording = timeRecording;
    }

    @Override
    public Object attachFrameObject(Object attachObject) {
        final Object before = this.frameObject;
        this.frameObject = attachObject;
        return before;
    }

    @Override
    public Object getFrameObject() {
        return this.frameObject;
    }

    @Override
    public Object detachFrameObject() {
        final Object delete = this.frameObject;
        this.frameObject = null;
        return delete;
    }

	public void setSequence(short s) {
		// TODO Auto-generated method stub
		
	}

	public void setDepth(int latestStackIndex) {
		// TODO Auto-generated method stub
		
	}
}

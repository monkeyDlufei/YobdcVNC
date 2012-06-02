package yobdc.rfb;

public class EncodeFactory {
	private Framebuffer framebuffer;
	private RfbProtocol rfb;
	
	public EncodeFactory(RfbProtocol _rfb, Framebuffer _framebuffer){
		this.rfb = _rfb;
		this.framebuffer = _framebuffer;
	}

	public Framebuffer getFramebuffer() {
		return framebuffer;
	}

	public void setFramebuffer(Framebuffer framebuffer) {
		this.framebuffer = framebuffer;
	}

	public RfbProtocol getRfb() {
		return rfb;
	}

	public void setRfb(RfbProtocol rfb) {
		this.rfb = rfb;
	}

}

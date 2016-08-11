package net.kalinovcic.kinetix.video;

import java.awt.Color;
import java.io.File;

import org.jcodec.api.FrameGrab;
import org.jcodec.common.io.FileChannelWrapper;

@SuppressWarnings("deprecation")
public class VideoSettings
{
	public File videoFile;
	public FileChannelWrapper videoChannel;
	public FrameGrab videoFrameGrabber;
	
	public boolean paused;
	public int frameIndex;
	
	public Color backgroundColor = new Color(140, 140, 140);
	public int cropLeft = 160;
	public int cropRight = 250;
	public int cropTop = 25;
	public int cropBottom = 25;
}

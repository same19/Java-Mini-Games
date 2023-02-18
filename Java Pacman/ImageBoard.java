import java.util.*;
import java.lang.*;
public interface ImageBoard {
	public String[][] getImages();
	public String getImageFolder();
	public String getBackgroundImage();
	public default ArrayList<PositionedImage> getTopImages() {
        return new ArrayList<PositionedImage>();
    }
}
package com.lessask.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;

/**
 * CameraHelper
 * @author wzystal@gmail.com
 * 2014-10-14
 */
public class CameraHelper {

    private static final String TAG = CameraHelper.class.getSimpleName();
    private static final CameraSizeComparator sizeComparator = new CameraSizeComparator();

	/**
	 * 根据预设宽高获取匹配的相机分辨率,若没有则返回中间值
	 * @param camera 相机
	 * @param width 预设宽度
	 * @param height 预设高度
	 * @return
	 */
	public static Size getOptimalPreviewSize(Camera camera, int width,
			int height) {
		Size previewSize = null;
		List<Size> supportedSizes = camera.getParameters()
				.getSupportedPreviewSizes();
		Collections.sort(supportedSizes, new SizeComparator());
		if (null != supportedSizes && supportedSizes.size() > 0) {
            boolean hasSize = false;
            for (Size size : supportedSizes) {
                float rate = size.height/(size.width*1f);
                Log.e("wzy.size", "当前手机支持的分辨率：" + size.width + "*" + size.height+", "+rate);
                if (null != size && size.width == width
                        && size.height == height) {
                    previewSize = size;
                    hasSize = true;
                    break;
                }
            }
            if (!hasSize) {
                previewSize = supportedSizes.get(supportedSizes.size() / 2);
            }
        }
		return previewSize;
	}

	private static class SizeComparator implements Comparator<Size> {
		@Override
		public int compare(Size size1, Size size2) {
			if (size1.height != size2.height)
				return size1.height - size2.height;
			else
				return size1.width - size2.width;
		}
	}

	/**
	 * 将屏幕坐标系转化成对焦坐标系,返回要对焦的矩形框
	 * @param x 横坐标
	 * @param y 纵坐标
	 * @param w 相机宽度
	 * @param h 相机高度
	 * @param areaSize 对焦区域大小
	 * @return
	 */
	public static Rect getFocusArea(int x, int y, int w, int h, int areaSize) {
		int centerX = x / w * 2000 - 1000;
		int centerY = y / h * 2000 - 1000;
		int left = clamp(centerX - areaSize / 2, -1000, 1000);
		int right = clamp(left + areaSize, -1000, 1000);
		int top = clamp(centerY - areaSize / 2, -1000, 1000);
		int bottom = clamp(top + areaSize, -1000, 1000);
		return new Rect(left, top, right, bottom);
	}

	/**
	 * 限定x取值范围为[min,max]
	 * @param x
	 * @param min
	 * @param max
	 * @return
	 */
	public static int clamp(int x, int min, int max) {
		if (x > max) {
            return max;
        }
		if (x < min) {
            return min;
        }
		return x;
	}
	public static Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth){
        Collections.sort(list, sizeComparator);
        int i = 0;
        for(Size s:list) {
            if ((s.width >= minWidth) && equalRate(s, th)) {
                float rate = s.width/(s.height*1f);
                Log.e(TAG, "PreviewSize:w = " + s.width + ",h = " + s.height+", rate:"+rate);
                break;
            }
            i++;
        }
        if(i == list.size()) {
            i = 0;//如果没找到，就选最小的size
        }
        return list.get(i);
    }
	public static Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth){
        Collections.sort(list, sizeComparator);
        int i = 0;
        for(Size s:list) {
            if ((s.width >= minWidth) && equalRate(s, th)) {
                float rate = s.width/(s.height*1f);
                Log.e(TAG, "PictureSize :w = " + s.width + ",h = " + s.height+", rate:"+rate);
                break;
            }
            i++;
        }
        if(i == list.size()) {
            i = 0;//如果没找到，就选最小的size
        }
        return list.get(i);
    }
    public static class CameraSizeComparator implements Comparator<Camera.Size>{
        //按升序排列
        public int compare(Size lhs, Size rhs) {
            // TODO Auto-generated method stub
            if(lhs.width == rhs.width) {
                return 0;
            }
            else if(lhs.width > rhs.width) {
                return 1;
            }
            else {
                return -1;
            }
        }

    }
    public static boolean equalRate(Size s, float rate){
        float r = (float)(s.width)/(float)(s.height);
        if(Math.abs(r - rate) <= 0.2) {
            return true;
        }
        else {
            return false;
        }
    }
}

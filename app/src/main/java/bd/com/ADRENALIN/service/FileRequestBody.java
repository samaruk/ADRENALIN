package bd.com.ADRENALIN.service;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by iqrasys on 8/28/2017.
 */

public class FileRequestBody extends RequestBody {
    private File mFile;
    private String mPath;
    private ProgressListener mListener;

    private static final int DEFAULT_BUFFER_SIZE = 2048;
    public interface ProgressListener {
        void onProgress(int percentage);
        void onFinish();
    }
    public FileRequestBody(final File file, final  ProgressListener listener) {
        mFile = file;
        mListener = listener;
    }
    @Override
    public MediaType contentType() {
        // i want to upload only images
        return MediaType.parse("image/*");
    }
    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }
    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;
        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {
                // update progress on UI thread
                handler.post(new ProgressUpdater(uploaded, fileLength));
                uploaded += read;
                sink.write(buffer, 0, read);
                //Log.e("","onProgress : "+100*uploaded/ fileLength);
            }
            mListener.onFinish();
        } finally {
            in.close();
        }
    }
    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;
        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }
        @Override
        public void run() {
            mListener.onProgress((int)(100 * mUploaded / mTotal));
        }
    }
}

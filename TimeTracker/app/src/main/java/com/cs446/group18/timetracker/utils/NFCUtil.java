package com.cs446.group18.timetracker.utils;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;


import com.cs446.group18.timetracker.MyApplication;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;


public class NFCUtil {
    public static final String MIME_TEXT_PLAIN = "text/plain";

    private static final String TAG = NFCUtil.class.getSimpleName();

    private static CodeListener nfcListener;

    public static void setNfcListener(CodeListener nfcListener) {
        NFCUtil.nfcListener = nfcListener;
    }

    public enum CodeType {
        NFC, QR
    }

    public interface CodeListener {
        void onCodeRetrieved(String code, CodeType codeType);
    }

    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author Ralf Wondratschek
     */
    private static class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
// NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();
            if (ndefMessage != null) {
                NdefRecord[] records = ndefMessage.getRecords();
                for (NdefRecord ndefRecord : records) {
                    if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && (Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT) || Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_URI))) {
                        try {
                            return readText(ndefRecord);
                        } catch (UnsupportedEncodingException e) {

                        }
                    }
                }
            }else
            {
                return "";
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            /*
             * See NFC forum specification for "Text Record Type Definition" at 3.2.1
             *
             * http://www.nfc-forum.org/specs/
             *
             * bit_7 defines encoding
             * bit_6 reserved for future use, must be 0
             * bit_5..0 length of IANA language code
             */

            byte[] payload;

            if (Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {
                return record.toUri().toString();
            } else {
                payload = record.getPayload();
            }

// Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

// Get the Language Code
            int languageCodeLength = payload[0] & 0063;

// String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
// e.g. "en"

// Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (nfcListener != null) {
//result = result.substring(result.lastIndexOf('/') + 1);
                    nfcListener.onCodeRetrieved(result, CodeType.NFC);
                }
            }
        }
    }

}
package com.connect.meetupsfellow.global.utils

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.log.LogManager
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class Multipart(url: URL, auth: String) {

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     * @param url
     * *
     * @throws IOException
     */

    companion object {
        private const val LINE_FEED = "\r\n"
        private const val maxBufferSize = 1024 * 1024
        private const val charset = "UTF-8"
    }

    // creates a unique boundary based on time stamp
    private val boundary: String = "===" + System.currentTimeMillis() + "==="
    private val httpConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
    private val outputStream: OutputStream
    private val writer: PrintWriter

    init {
        httpConnection.setRequestProperty("Accept-Charset", "UTF-8")
        httpConnection.setRequestProperty("Connection", "Keep-Alive")
        httpConnection.setRequestProperty("Cache-Control", "no-cache")
        httpConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
        httpConnection.setRequestProperty("Accept", "application/json")
        httpConnection.setRequestProperty("Authorization", auth)
        httpConnection.setChunkedStreamingMode(maxBufferSize)
        httpConnection.doInput = true
        httpConnection.doOutput = true    // indicates POST method
        httpConnection.useCaches = false
        outputStream = httpConnection.outputStream
        writer = PrintWriter(OutputStreamWriter(outputStream, charset), true)
    }

    /**
     * Adds a form field to the request
     * @param name  field name
     * *
     * @param value field value
     */
    fun addFormField(name: String, value: String) {
        writer.append("--").append(boundary).append(LINE_FEED)
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"")
            .append(LINE_FEED)
        writer.append(LINE_FEED)
        writer.append(value).append(LINE_FEED)
        writer.flush()
    }

    /**
     * Adds a upload file section to the request
     * @param fieldName  - name attribute in <input type="file" name="..."></input>
     * *
     * @param uploadFile - a File to be uploaded
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun addFilePart(fieldName: String, uploadFile: File, fileName: String, fileType: String) {
        writer.append("--").append(boundary).append(LINE_FEED)
        writer.append("Content-Disposition: file; name=\"").append(fieldName)
            .append("\"; filename=\"").append(fileName).append("\"").append(LINE_FEED)
        writer.append("Content-Type: ").append(fileType).append(LINE_FEED)
        writer.append(LINE_FEED)
        writer.flush()

        val inputStream = FileInputStream(uploadFile)
        inputStream.copyTo(outputStream, maxBufferSize)

        outputStream.flush()
        inputStream.close()
        writer.append(LINE_FEED)
        writer.flush()
    }


    /*  @Throws(IOException::class)
      fun addFilePartArray(fieldName: String, uploadFile:  ArrayList<File>, fileName: String, fileType: String) {
          writer.append("--").append(boundary).append(LINE_FEED)
          writer.append("Content-Disposition: file; name=\"").append(fieldName)
                  .append("\"; filename=\"").append(fileName).append("\"").append(LINE_FEED)
          writer.append("Content-Type: ").append(fileType).append(LINE_FEED)
          writer.append(LINE_FEED)
          writer.flush()

          val inputStream = FileInputStream(uploadFile)
          inputStream.copyTo(outputStream, maxBufferSize)

          outputStream.flush()
          inputStream.close()
          writer.append(LINE_FEED)
          writer.flush()
      }

  */
    /**
     * Adds a header field to the request.
     * @param name  - name of the header field
     * *
     * @param value - value of the header field
     */
    fun addHeaderField(name: String, value: String) {
        writer.append("$name: $value").append(LINE_FEED)
        writer.flush()
    }

    /**
     * Upload the file and receive a response from the server.
     * @param onFileUploadedListener
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun upload(onFileUploadedListener: OnFileUploadedListener?) {
        writer.append(LINE_FEED).flush()
        writer.append("--").append(boundary).append("--")
            .append(LINE_FEED)
        writer.close()

        try {
            // checks server's status code first
            val status = httpConnection.responseCode
            if (status == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(
                    InputStreamReader(
                        httpConnection
                            .inputStream
                    )
                )
                val response = reader.use(BufferedReader::readText)
                LogManager.logger.i(ArchitectureApp.instance!!.tag, "Response_is ::: $response")
                httpConnection.disconnect()
                onFileUploadedListener?.onFileUploadingSuccess(response)
            }
            else if (status == HttpURLConnection.HTTP_CREATED) {
                val errorStream = httpConnection.inputStream
                if (errorStream != null) {
                    val reader = BufferedReader(InputStreamReader(errorStream))
                    val response = reader.use(BufferedReader::readText)
                    LogManager.logger.i(ArchitectureApp.instance!!.tag, "Response_is_multipart::: $response")
                    onFileUploadedListener?.onFileUploadingFailed(response)
                } else {
                    LogManager.logger.e(ArchitectureApp.instance!!.tag, "$errorStream")
                    onFileUploadedListener?.onFileUploadingFailed("Something went wrong!")
                }
            }
            else {
                val reader = BufferedReader(
                    InputStreamReader(
                        httpConnection
                            .errorStream
                    )
                )
                val response = reader.use(BufferedReader::readText)
                LogManager.logger.i(ArchitectureApp.instance!!.tag, "Response_is_multipart2::: $response")
                onFileUploadedListener?.onFileUploadingFailed(response)

            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


}

interface OnFileUploadedListener {
    fun onFileUploadingSuccess(response: String)

    fun onFileUploadingFailed(error: String)
}

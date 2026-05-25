package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.interop.LocalUIViewController
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    val byteArray = ByteArray(size)
    if (size > 0) {
        byteArray.usePinned { pinned ->
            memcpy(pinned.addressOf(0), bytes, length)
        }
    }
    return byteArray
}

class DocumentPickerDelegate(
    private val onPicked: (NSURL) -> Unit,
    private val onCancelled: () -> Unit
) : NSObject(), UIDocumentPickerDelegateProtocol {
    
    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
        if (url != null) {
            onPicked(url)
        } else {
            onCancelled()
        }
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        onCancelled()
    }
}

class GalleryPickerDelegate(
    private val onPicked: (ByteArray, String) -> Unit,
    private val onCancelled: () -> Unit
) : NSObject(), PHPickerViewControllerDelegateProtocol {
    
    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, null)
        
        val result = didFinishPicking.firstOrNull() as? PHPickerResult
        if (result == null) {
            onCancelled()
            return
        }
        
        val itemProvider = result.itemProvider
        val typeIdentifier = itemProvider.registeredTypeIdentifiers.firstOrNull() as? String
        if (typeIdentifier == null) {
            onCancelled()
            return
        }
        
        itemProvider.loadFileRepresentationForTypeIdentifier(typeIdentifier) { url, error ->
            if (url != null) {
                try {
                    val nsData = NSData.dataWithContentsOfURL(url)
                    if (nsData != null) {
                        val bytes = nsData.toByteArray()
                        val ext = url.pathExtension?.lowercase() ?: ""
                        val resolvedMimeType = getMimeTypeFromExtension(ext)
                        
                        dispatch_async(dispatch_get_main_queue()) {
                            onPicked(bytes, resolvedMimeType)
                        }
                        return@loadFileRepresentationForTypeIdentifier
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            dispatch_async(dispatch_get_main_queue()) {
                onCancelled()
            }
        }
    }
}

@Composable
actual fun rememberPlatformMediaPicker(onMediaPicked: (ByteArray, String) -> Unit): PlatformMediaPicker {
    val viewController = LocalUIViewController.current
    
    return remember {
        object : PlatformMediaPicker {
            private var documentDelegate: DocumentPickerDelegate? = null
            private var galleryDelegate: GalleryPickerDelegate? = null

            override fun launch(mimeType: String) {
                val useGallery = mimeType.contains("image") || mimeType.contains("video")
                
                if (useGallery) {
                    val configuration = PHPickerConfiguration()
                    configuration.selectionLimit = 1
                    
                    if (mimeType.contains("video")) {
                        configuration.filter = PHPickerFilter.videosFilter()
                    } else if (mimeType.contains("image")) {
                        configuration.filter = PHPickerFilter.imagesFilter()
                    }
                    
                    val picker = PHPickerViewController(configuration = configuration)
                    val delegate = GalleryPickerDelegate(
                        onPicked = { bytes, resolvedMimeType ->
                            onMediaPicked(bytes, resolvedMimeType)
                            galleryDelegate = null
                        },
                        onCancelled = {
                            galleryDelegate = null
                        }
                    )
                    
                    galleryDelegate = delegate
                    picker.delegate = delegate
                    
                    viewController.presentViewController(picker, animated = true, completion = null)
                } else {
                    val utiTypes = listOf("public.item")
                    val picker = UIDocumentPickerViewController(documentTypes = utiTypes, inMode = UIDocumentPickerMode.UIDocumentPickerModeImport)
                    
                    val delegate = DocumentPickerDelegate(
                        onPicked = { url ->
                            val shouldStopAccessing = url.startAccessingSecurityScopedResource()
                            try {
                                val nsData = NSData.dataWithContentsOfURL(url)
                                if (nsData != null) {
                                    val bytes = nsData.toByteArray()
                                    val ext = url.pathExtension?.lowercase() ?: ""
                                    val resolvedMimeType = getMimeTypeFromExtension(ext)
                                    onMediaPicked(bytes, resolvedMimeType)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                if (shouldStopAccessing) {
                                    url.stopAccessingSecurityScopedResource()
                                }
                            }
                            documentDelegate = null
                        },
                        onCancelled = {
                            documentDelegate = null
                        }
                    )
                    
                    documentDelegate = delegate
                    picker.delegate = delegate
                    
                    viewController.presentViewController(picker, animated = true, completion = null)
                }
            }
        }
    }
}

private fun getMimeTypeFromExtension(extension: String): String {
    return when (extension) {
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "heic" -> "image/heic"
        "heif" -> "image/heif"
        "gif" -> "image/gif"
        "webp" -> "image/webp"
        "mp4" -> "video/mp4"
        "mov" -> "video/quicktime"
        "m4v" -> "video/x-m4v"
        "pdf" -> "application/pdf"
        "txt" -> "text/plain"
        "json" -> "application/json"
        else -> "application/octet-stream"
    }
}

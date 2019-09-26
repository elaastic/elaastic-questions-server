package org.elaastic.questions.attachment

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.GetMapping



@Controller
class AttachmentController(
        @Autowired val attachmentService: AttachmentService
) {

    @GetMapping("/attachment/{id}")
    @ResponseBody
    fun serveAttachment(@PathVariable id: Long): ResponseEntity<Resource>? {
        val attachment = attachmentService.getAttachmentById(id)
        InputStreamResource(attachmentService.getInputStreamForAttachment(attachment)).let {
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"${attachment.originalFileName}\"").body<Resource>(it)
        }
    }

}

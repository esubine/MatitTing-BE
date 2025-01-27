package com.kr.matitting.controller;

import com.kr.matitting.s3.S3Uploader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {
    private final S3Uploader s3Uploader;

    @Operation(summary = "이미지 s3 업로드", description = "이미지 s3 업로드 API 입니다. \n\n" +
                                                        "[로직설명] \n\n"  +
                                                        "MULTIPART_FORM_DATA_VALUE 타입으로 이미지 추가 후 API 요청 시 s3에 업로드하는 로직을 거친 뒤 s3 이미지 주소가 반환됩니다. \n\n")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schemaProperties = {
                    @SchemaProperty(name = "imgUrl", schema = @Schema(type = "string", description = "imgUrl"))}))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestPart(value = "image") MultipartFile multipartFile
    ) {
        Map<String, String> imgUrl = s3Uploader.upload(multipartFile);
        return ResponseEntity.ok().body(imgUrl);
    }
}

package com.bookinline.bookinline.service;
import com.bookinline.bookinline.service.impl.ImageServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class ImageServiceIntegrationTest {
    @InjectMocks
    private ImageServiceImpl imageService;

    @Test
    public void ImageService_UploadImage_ReturnsImagePath() {
        byte[] fakeImageData = new byte[1024]; // 1KB fake image data
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                fakeImageData
        );

        String imagePath = imageService.uploadImage(file);
        Assertions.assertThat(imagePath).isNotNull();
        Assertions.assertThat(imagePath).startsWith("/images/");
        Assertions.assertThat(imagePath).endsWith("test-image.jpg");
    }
}

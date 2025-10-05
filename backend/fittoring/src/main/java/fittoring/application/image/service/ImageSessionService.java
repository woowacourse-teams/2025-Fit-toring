package fittoring.application.image.service;

import fittoring.application.image.repository.ImageRepository;
import fittoring.application.image.repository.ImageSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ImageSessionService {

    private final ImageRepository imageRepository;
    private final ImageSessionRepository imageSessionRepository;
}

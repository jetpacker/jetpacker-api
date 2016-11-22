package jetpacker.api.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.AsyncRestTemplate

/**
 * Created by donny on 22/11/16.
 */
@Service
class RepositoryService {
    private final AsyncRestTemplate asyncRestTemplate

    @Autowired
    RepositoryService(AsyncRestTemplate asyncRestTemplate) {
        this.asyncRestTemplate = asyncRestTemplate
    }
}

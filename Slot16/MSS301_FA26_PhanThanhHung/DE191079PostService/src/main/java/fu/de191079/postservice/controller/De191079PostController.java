package fu.de191079.postservice.controller;

import fu.de191079.postservice.dto.ApiResponse;
import fu.de191079.postservice.dto.PostDTO;
import fu.de191079.postservice.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class De191079PostController {

    private final PostService postService;

    public De191079PostController(PostService postService) {
        this.postService = postService;
    }

    // 1. Create Post: 201/1, 406/2, 404/4, 400/5, 500/0
    @PostMapping
    public ResponseEntity<ApiResponse> createPost(@Valid @RequestBody PostDTO postDTO, BindingResult br) {
        if (br.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE) // 406
                    .body(ApiResponse.error(2, firstError(br)));
        }
        return map(postService.createPost(postDTO), HttpStatus.CREATED); // 201 on success
    }

    // 2. Update Post: 200/1, 406/2, 404/4, 500/0
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse> updatePost(@PathVariable Long postId,
                                                  @RequestBody PostDTO postDTO) {
        return map(postService.updatePost(postId, postDTO), HttpStatus.OK); // 200 on success
    }

    // 3. Get Post Detail: 200/1, 404/4, 500/0
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse> getPostById(@PathVariable Long postId) {
        return map(postService.getPostById(postId), HttpStatus.OK);
    }

    // 4. Cancel Post: 200/1, 404/4, 500/0
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse> cancelPost(@PathVariable Long postId) {
        return map(postService.cancelPost(postId), HttpStatus.OK);
    }

    // 5. Get Post List: 200/1, 406/2, 500/0
    @GetMapping
    public ResponseEntity<ApiResponse> getPostList(
            @RequestParam(required = false) String advertiserName,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (size < 1 || size > 100) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE) // 406
                    .body(ApiResponse.error(2, "size must be between 1 and 100"));
        }
        Pageable pageable = PageRequest.of(page, size);
        return map(postService.getPostList(advertiserName, status, pageable), HttpStatus.OK);
    }

    private String firstError(BindingResult br) {
        return br.getAllErrors().isEmpty() ? "Validation failed" : br.getAllErrors().get(0).getDefaultMessage();
    }

    // map service status -> HTTP code
    private ResponseEntity<ApiResponse> map(ApiResponse apiResponse, HttpStatus okStatus) {
        int status = apiResponse.getStatus();
        HttpStatus httpStatus;
        switch (status) {
            case 1: // success
                httpStatus = okStatus;
                break;
            case 2: // validation error
                httpStatus = HttpStatus.NOT_ACCEPTABLE; // 406
                break;
            case 3: // duplicate
                httpStatus = HttpStatus.IM_USED; // 226
                break;
            case 4: // not found
                httpStatus = HttpStatus.NOT_FOUND; // 404
                break;
            case 5: // user not ACTIVE
                httpStatus = HttpStatus.BAD_REQUEST; // 400
                break;
            default: // server error
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // 500
                break;
        }
        return ResponseEntity.status(httpStatus).body(apiResponse);
    }
}


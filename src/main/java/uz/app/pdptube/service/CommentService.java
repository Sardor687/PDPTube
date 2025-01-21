package uz.app.pdptube.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.app.pdptube.entity.Comment;
import uz.app.pdptube.entity.Video;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.CommentRepository;
import uz.app.pdptube.repository.VideoRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;

    //Error otyapti (2-martalik), keyin to'g'irlayman
    /*public ResponseMessage getVideoComments(Integer videoId) {
       *//* Optional<Video> optionalVideo = videoRepository.findById(videoId);
        if (!optionalVideo.isPresent()) {
            return new ResponseMessage(false, "video doesn't exist with this videoId: " + videoId, null);
        }*//*

        Optional<List<Comment>> optionalComments = commentRepository.findAllByVideoId(videoId);
        if (optionalComments.isPresent()) {
            List<Comment> comments = optionalComments.get();
            int size = comments.size();
            if (size > 0) {
                return new ResponseMessage(true, "here are the comments to the videoId "+ videoId, comments );
            }else {
                return new ResponseMessage(true, "there are no comments to the videoId "+ videoId, comments );
            }
        }else {
            return new ResponseMessage(false, "no comments found for this video id", videoId);
        }
    }*/
}

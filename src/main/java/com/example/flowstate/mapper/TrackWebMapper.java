package com.example.flowstate.mapper;
import com.example.flowstate.dto.request.TrackRequest;
import com.example.flowstate.dto.response.TrackResponse;
import com.example.flowstate.model.Track;
import org.springframework.stereotype.Component;
@Component
public class TrackWebMapper {
    public Track toEntity(TrackRequest request){
        Track track = new Track();
        track.setTitle(request.title());
        track.setCategory(request.category());
        track.setDeadline(request.deadline());
        return track;
    }
    public TrackResponse toResponse(Track track){
        return new TrackResponse(
                track.getId(),
                track.getTitle(),
                track.getCategory(),
                track.getDeadline(),
                track.getCreatedAt()
        );
    }
}

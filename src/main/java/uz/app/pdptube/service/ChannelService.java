package uz.app.pdptube.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.app.pdptube.dto.ChannelDTO;
import uz.app.pdptube.entity.Channel;
import uz.app.pdptube.entity.Channel_Owner;
import uz.app.pdptube.entity.User;
import uz.app.pdptube.helper.Helper;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.ChannelOwnerRepository;
import uz.app.pdptube.repository.ChannelRepository;
import uz.app.pdptube.repository.UserRepository;
import uz.app.pdptube.repository.VideoRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final ChannelOwnerRepository channelOwnerRepository;
    private final VideoRepository videoRepository;

    public ResponseMessage getChannel(){
        User principal = Helper.getCurrentPrincipal();
        Optional<Channel_Owner> optionalChannelOwner = channelOwnerRepository.findByOwner(principal.getId());
        if (optionalChannelOwner.isPresent()) {
            Channel_Owner channelOwner = optionalChannelOwner.get();
            Optional<Channel> optionalChannel = channelRepository.findById(channelOwner.getChannel());
            if (optionalChannel.isPresent()) {
                Channel channel = optionalChannel.get();
                return new ResponseMessage(true, "Here is the channel",channel);
            }else {
                return new ResponseMessage(false, "channel_owner da kanal bor ekan , lekin bazada yo'q , bu xato yuzaga kelsa , backendda xato bor", channelOwner);
            }
        }else {
            return new ResponseMessage(false, "you don't have channel, but you can create it", principal);
        }
    }




    public ResponseMessage createChannel(ChannelDTO channelDTO){
        Optional<Channel_Owner> relation = channelOwnerRepository.findByOwner(Helper.getCurrentPrincipal().getId());
        if (relation.isPresent()) {
            Optional<Channel> byId = channelRepository.findById(relation.get().getChannel());
            if (byId.isPresent()) {
                return new ResponseMessage(false, "you already have a channel", byId.get());
            }else {
                return new ResponseMessage(false, "channel_owner relation bor , channelni ozi yoq, backend bu yerda xato ishladi", relation.get());
            }
        }else {
            Channel newChannel = Channel.builder()
                    .description(channelDTO.getDescription())
                    .profilePicture(channelDTO.getProfilePicture())
                    .name(channelDTO.getName())
                    .build();
            channelRepository.save(newChannel);
            Channel_Owner channelOwner = new Channel_Owner();
            channelOwner.setOwner(Helper.getCurrentPrincipal().getId());
            channelOwner.setChannel(newChannel.getId());
            channelOwnerRepository.save(channelOwner);
            return new ResponseMessage(true, "Your channel has been created", newChannel);
        }


          /*  Optional<Channel> optionalChannel = channelRepository.findByOwnerId(principal.getId());
            if (optionalChannel.isPresent()) {
                return new ResponseMessage(false, "you already have a channel", channel);
            }else {

                principal.setChannel(newChannel);
                channelRepository.save(newChannel);
                userRepository.save(principal);
                return new ResponseMessage(true, "channel created", newChannel);
            }
        }else {
            return new ResponseMessage(false, "you already have a channel", channel);
        }*/
    }


    @Transactional
    public ResponseMessage deleteChannel() {
       User principal = Helper.getCurrentPrincipal();
        Optional<Channel_Owner> relation = channelOwnerRepository.findByOwner(principal.getId());
        if (relation.isPresent()) {
            Channel_Owner channelOwner = relation.get();
            Optional<Channel> optionalChannel = channelRepository.findById(channelOwner.getChannel());
            if (optionalChannel.isPresent()) {
                Channel channel = optionalChannel.get();
                deleteAllVideosByChannelId(channel);
                channelRepository.delete(channel);
                channelOwnerRepository.delete(channelOwner);
                return new ResponseMessage(true, "Your channel has been deleted", channelOwner);
            }else {
                return new ResponseMessage(false, "channel_owner relation bor , lekin channel yo'q , demak backendda xato", channelOwner);
            }
        }else {
            return new ResponseMessage(false, "you don't have a channel", principal);
        }
    }
    public void deleteAllVideosByChannelId(Channel channel) {
        videoRepository.deleteByChannel(channel);
    }
}


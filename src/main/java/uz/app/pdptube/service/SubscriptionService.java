package uz.app.pdptube.service;

import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;
import uz.app.pdptube.entity.Channel;
import uz.app.pdptube.entity.ChannelOwner;
import uz.app.pdptube.entity.Subscription;
import uz.app.pdptube.entity.User;
import uz.app.pdptube.helper.Helper;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.ChannelOwnerRepository;
import uz.app.pdptube.repository.ChannelRepository;
import uz.app.pdptube.repository.SubscriptionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final ChannelOwnerRepository channelOwnerRepository;
    private final ChannelService channelService;
    private final ChannelRepository channelRepository;

    public ResponseMessage getSubscriptions() {
        User principal = Helper.getCurrentPrincipal();
        List<Subscription> userSubscriptions = subscriptionRepository.findAllByFollower(principal.getId());
        if (userSubscriptions.isEmpty()) {
            return new ResponseMessage(true, "Seems like you haven't followed anyone yet", userSubscriptions);
        } else {
            List<Integer> channelIds = userSubscriptions.stream().map(sub -> sub.getChannel()).toList();
            List<Channel> channels = new ArrayList<>();
            for (Integer channelId : channelIds)
                channelRepository.findById(channelId).ifPresent(channel -> channels.add(channel));

            return new ResponseMessage(true, "Here are all of the channels you have subscribed to", channels);
        }
    }


    public ResponseMessage subscribeToChannel(Integer channelId) {
        User currentUser = Helper.getCurrentPrincipal();
        if (!channelRepository.findById(channelId).isPresent()) {
            return new ResponseMessage(false, "Channel with this idea doesn't exist", channelId);
        }


        Optional<ChannelOwner> channelOwner = channelOwnerRepository.findById(channelId);
        if (channelOwner.isEmpty()) {
            return new ResponseMessage(false, "channelOwner relationda yoq , lekin Channel bor , backenda xato", channelId);
        } else {



            if (channelOwner.get().getOwner().equals(currentUser.getId())) {
                return new ResponseMessage(false, "You cannot subscribe to your own channel", null);
            }


            boolean existingSubscription = subscriptionRepository.existsByChannelAndFollower(channelId, currentUser.getId());
            if (existingSubscription) {
                return new ResponseMessage(false, "You are already subscribed to this channel", channelId);
            }


            Subscription newFollower = new Subscription();
            newFollower.setChannel(channelId);
            newFollower.setFollower(currentUser.getId());
            subscriptionRepository.save(newFollower);

            return new ResponseMessage(true, "Successfully subscribed to the channel", newFollower);
        }

    }

    public ResponseMessage unsubscribeFromChannel(Integer channelId) {
        User currentUser = Helper.getCurrentPrincipal();

        Optional<Subscription> subscription = subscriptionRepository.findByChannelAndFollower(channelId, currentUser.getId());

        if (subscription.isEmpty()) {
            return new ResponseMessage(false, "Subscription not found", null);
        }

        subscriptionRepository.delete(subscription.get());

        return new ResponseMessage(true, "Successfully unsubscribed from the channel", null);
    }
}

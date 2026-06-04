package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.service;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.DeviceCommand;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.repository.DeviceCommandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommandService {

    private final DeviceCommandRepository commandRepository;
    private final SocketPublisher socketPublisher;

    public CommandService(DeviceCommandRepository commandRepository, SocketPublisher socketPublisher) {
        this.commandRepository = commandRepository;
        this.socketPublisher = socketPublisher;
    }

    @Transactional
    public DeviceCommand queueCommand(String deviceId, String commandText) {
        DeviceCommand cmd = new DeviceCommand(deviceId, commandText);
        cmd = commandRepository.save(cmd);
        
        // Broadcast the command event via WebSocket so if device is connected via WS, it can act on it
        socketPublisher.publish("device-command", cmd);
        return cmd;
    }

    public List<DeviceCommand> getPendingCommands(String deviceId) {
        return commandRepository.findAllByDeviceIdAndStatusOrderByCreatedAtDesc(deviceId, "pending");
    }

    @Transactional
    public Optional<DeviceCommand> respondToCommand(String deviceId, Long commandId, String status, String responseText) {
        Optional<DeviceCommand> cmdOpt = commandRepository.findById(commandId);
        if (cmdOpt.isPresent()) {
            DeviceCommand cmd = cmdOpt.get();
            if (cmd.getDeviceId().equals(deviceId)) {
                cmd.setStatus(status);
                cmd.setExecutedAt(LocalDateTime.now());
                cmd.setResponse(responseText);
                commandRepository.save(cmd);

                // Publish command response event to dashboard operators in real-time
                socketPublisher.publish("command-response", cmd);
                return Optional.of(cmd);
            }
        }
        return Optional.empty();
    }

    public List<DeviceCommand> getCommandHistory(String deviceId) {
        return commandRepository.findAllByDeviceIdOrderByCreatedAtDesc(deviceId);
    }
}

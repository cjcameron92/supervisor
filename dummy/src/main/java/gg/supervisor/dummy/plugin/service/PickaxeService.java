package gg.supervisor.dummy.plugin.service;

import gg.supervisor.core.annotation.Component;
import gg.supervisor.dummy.plugin.model.PickaxeData;
import gg.supervisor.dummy.plugin.repository.PickaxeRepository;
import org.bukkit.inventory.ItemStack;

@Component
public class PickaxeService {

    public PickaxeService(PickaxeRepository repository) {
        ItemStack itemStack = null;
        PickaxeData pickaxeData = null;
        
        repository.save(itemStack, pickaxeData);
        PickaxeData data = repository.find(itemStack);
        if (repository.containsKey(itemStack)) {
            // TODO: 2024-11-02 implement logic 
        }
        
    }
}

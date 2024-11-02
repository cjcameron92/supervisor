package gg.supervisor.dummy.plugin.repository;

import gg.supervisor.core.annotation.Component;
import gg.supervisor.dummy.plugin.model.PickaxeData;
import gg.supervisor.repository.itemstack.ItemStackRepository;

@Component
public interface PickaxeRepository extends ItemStackRepository<PickaxeData> {

}

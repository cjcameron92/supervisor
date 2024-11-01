package gg.supervisor.dummy.plugin.repository;

import gg.supervisor.core.annotation.Component;
import gg.supervisor.core.repository.JsonPlayerRepository;
import gg.supervisor.dummy.plugin.model.PerkData;

@Component
public interface PlayerPerkRepository extends JsonPlayerRepository<PerkData> {

}

package ml.echelon133.sportevents.stadium;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StadiumServiceImpl implements StadiumService {

    private StadiumRepository stadiumRepository;

    @Autowired
    public StadiumServiceImpl(StadiumRepository stadiumRepository) {
        this.stadiumRepository = stadiumRepository;
    }

    @Override
    public Stadium convertDtoToEntity(StadiumDto stadiumDto) {
        return new Stadium(stadiumDto.getName(), stadiumDto.getCity(), stadiumDto.getCapacity());
    }

    @Override
    public Stadium save(Stadium stadium) {
        return stadiumRepository.save(stadium);
    }

    @Override
    public List<Stadium> findAll() {
        return stadiumRepository.findAll();
    }

    @Override
    public Stadium findById(Long id) throws ResourceDoesNotExistException {
        Optional<Stadium> stadium = stadiumRepository.findById(id);
        if (stadium.isPresent()) {
            return stadium.get();
        }
        throw new ResourceDoesNotExistException("Stadium with this id does not exist");
    }

    @Override
    public boolean deleteById(Long id) {
        boolean exists = stadiumRepository.existsById(id);
        if (exists) {
            stadiumRepository.deleteById(id);
            exists = stadiumRepository.existsById(id);
        }
        return !exists;
    }
}

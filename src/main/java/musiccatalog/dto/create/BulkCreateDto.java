package musiccatalog.dto.create;

import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkCreateDto<T> {

    @Size(min = 1, message = "Минимальный размер bulk-операции - 1")
    private List<T> items = new ArrayList<>();

}

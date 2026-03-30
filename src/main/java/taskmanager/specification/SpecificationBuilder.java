package taskmanager.specification;

import org.springframework.data.jpa.domain.Specification;

public class SpecificationBuilder<T>
{
    private Specification<T> specification = Specification.allOf();

    public SpecificationBuilder<T> and(boolean condition, Specification<T> newSpecification)
    {
        if (condition)
        {
            specification = specification.and(newSpecification);
        }

        return this;
    }

    public Specification<T> build()
    {
        return specification;
    }
}
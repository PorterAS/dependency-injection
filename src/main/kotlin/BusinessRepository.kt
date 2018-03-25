interface BusinessRepository {
    fun getData(identifier: String): String

}

class BusinessRepositoryImpl : BusinessRepository {
    override fun getData(identifier: String): String {
        return "Hello World!"
    }

}

interface BusinessService {

    fun getData(identifier: String): String

}

class BusinessServiceImpl(private val businessRepository: BusinessRepository) : BusinessService {
    override fun getData(identifier: String): String {
        return businessRepository.getData(identifier)
    }

}